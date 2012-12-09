package com.hypermurea.hslpushdroid.reittiopas;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class FindStopsByLocationAsyncTask extends AsyncTask<Location,Void,Set<String>> {

	private static final String TAG = "FindStopsByLocationAsyncTask";
	private static final int SEARCH_DIAMETER_METERS = 200;

	private String user;
	private String password;
	private String serviceUrl;

	private TaskResultListener<Set<String>> listener;
	private Set<String> nearbyLineCodes;

	// reittiopas api wants coordinates in format lon/lat
	// api.reittiopas.fi/hsl/prod/?request=stops_area&epsg_in=wgs84&center_coordinate=24.88083,60.19701
	
	public FindStopsByLocationAsyncTask(String serviceUrl, String user, String password, TaskResultListener<Set<String>> listener) {
		super();
		this.serviceUrl = serviceUrl;
		this.user = user;
		this.password = password;
		this.listener = listener;
		nearbyLineCodes = new HashSet<String>();
	}

	@Override 
	public void onPreExecute() {
	}


	@Override
	protected Set<String> doInBackground(Location... location) {
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?user={0}&pass={1}&request={2}&epsg_in=wgs84&format=json&center_coordinate={3},{4}&diameter={5}");

		String[] args = {
				user, 
				password, 
				"stops_area", 
				String.valueOf(location[0].getLongitude()), 
				String.valueOf(location[0].getLatitude()),
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
				//String stopCode = response.getJSONObject(i).getString("code");
				//nearbyLineCodes.addAll(getPassingLineCodes(stopCode));
				stopCodes.add(response.getJSONObject(i).getString("code"));
			}

			//FindLinesByNameAsyncTask findByNameTask = new FindLinesByNameAsyncTask(serviceUrl, user, password, listener);
			//findByNameTask.execute(nearbyLineCodes.toArray(new String[0]));

		} catch(Exception e) {
			Log.e(TAG, "find stops by location failed", e);
		}

		return stopCodes;
	}

	private Set<String> getPassingLineCodes(String stopCode) throws Exception {
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?user={0}&pass={1}&request={2}&format=json&code={3}");

		String[] args = {user, password, "stop", stopCode};		

		Log.d(TAG, baseUrl.format(args));

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl.format(args));	

		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseString = httpClient.execute(request, responseHandler);
		JSONObject stopJson = new JSONArray(responseString).getJSONObject(0);

		Set<String> passingLineCodes = new HashSet<String>();
		JSONArray linesPassingStopJson = stopJson.getJSONArray("lines");
		for(int i = 0; i < linesPassingStopJson.length(); i ++) {
			passingLineCodes.add(linesPassingStopJson.get(i).toString().split(":")[0]);
		}

		return passingLineCodes;
	}


	@Override
	public void onPostExecute(Set<String> result) {
		listener.receiveResults(result);
	}

}
