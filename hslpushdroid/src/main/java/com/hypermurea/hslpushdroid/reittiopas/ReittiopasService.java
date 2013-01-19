package com.hypermurea.hslpushdroid.reittiopas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

public class ReittiopasService {
	
	private static final String TAG = "FindStopsByLocationAsyncTask";

	private String user;
	private String password;
	private String serviceUrl;
	
	public ReittiopasService(String user, String password, String serviceUrl) {
		this.user = user;
		this.password = password;
		this.serviceUrl = serviceUrl;
	}
	
	public List<TransportLine> findTransportLinesByName(String... searchTerms) {
		List<TransportLine> searchResult = null;	

		try {

			MessageFormat baseUrl = 
					new MessageFormat(serviceUrl + "?request=lines&format=json&user={0}&pass={1}&query={2}&p=111001");

			String[] args = {user, password, buildQuery(searchTerms)};

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(baseUrl.format(args));

			Log.d(TAG, baseUrl.format(args));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();

			searchResult = new ArrayList<TransportLine>();

			String responseString = httpClient.execute(request, responseHandler);

			if(responseString.length() > 0) {
				JSONArray response = new JSONArray(responseString);

				HashMap<String, TransportLine> resultHash = new HashMap<String, TransportLine>();
				for( int i = 0; i < response.length(); i ++) {
					JSONObject lineJson = response.getJSONObject(i);
					TransportLine line = new TransportLine(
							lineJson.getString("code_short"),
							lineJson.getString("code"),
							lineJson.getInt("transport_type_id"), 
							lineJson.getString("name"));

					if(!resultHash.containsKey(line.code)) {
						resultHash.put(line.code, line);
						searchResult.add(line);
					} 
				}	
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(JSONException e) {
			e.printStackTrace();
		}

		return searchResult;
	}
	
	private String buildQuery(String... params) throws UnsupportedEncodingException {
		String query = "";
		for(String s: params) {
			query += s + "|";
		}
		Log.d(TAG, "building query: " + query);
		return URLEncoder.encode(query.substring(0,  query.length() - 1), "utf-8");
	}
	
	public Set<String> findLinesPassingStop(String stop) {
		Set<String> passingLineCodes = new HashSet<String>();

		try {
			MessageFormat baseUrl = 
					new MessageFormat(serviceUrl + "?user={0}&pass={1}&request={2}&format=json&code={3}&p=0000001");

			String[] args = {user, password, "stop", stop};		

			Log.d(TAG, baseUrl.format(args));

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(baseUrl.format(args));	

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseString = httpClient.execute(request, responseHandler);
			JSONObject stopJson = new JSONArray(responseString).getJSONObject(0);

			JSONArray linesPassingStopJson = stopJson.getJSONArray("lines");
			for(int i = 0; i < linesPassingStopJson.length(); i ++) {
				passingLineCodes.add(linesPassingStopJson.get(i).toString().split(":")[0]);
			}

		} catch(Exception e) {
			Log.e(TAG, "FindLinesPassingStopAsyncTask failed", e);
		}
		return passingLineCodes;
	}
	
	// reittiopas api wants coordinates in format lon/lat
	// api.reittiopas.fi/hsl/prod/?request=stops_area&epsg_in=wgs84&center_coordinate=24.88083,60.19701
	public Set<String> findStopsByLocation(Location location) {
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?user={0}&pass={1}&request={2}&epsg_in=wgs84&format=json&center_coordinate={3},{4}&diameter={5}&p=1");

		final int SEARCH_DIAMETER_METERS = 200;
		String[] args = {
				user, 
				password, 
				"stops_area", 
				String.valueOf(location.getLongitude()), 
				String.valueOf(location.getLatitude()),
				String.valueOf(SEARCH_DIAMETER_METERS)};		

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl.format(args));

		Log.d(TAG, baseUrl.format(args));
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		Set<String> stopCodes = new HashSet<String>();
		
		try {
			String responseString = httpClient.execute(request, responseHandler);
			JSONArray response = new JSONArray(responseString);

			for(int i = 0; i < response.length(); i ++) {
				stopCodes.add(response.getJSONObject(i).getString("code"));
			}

		} catch(Exception e) {
			Log.e(TAG, "find stops by location failed", e);
		}

		return stopCodes;
	}


}
