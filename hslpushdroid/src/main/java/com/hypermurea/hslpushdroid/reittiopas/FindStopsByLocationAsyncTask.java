package com.hypermurea.hslpushdroid.reittiopas;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class FindStopsByLocationAsyncTask extends AsyncTask<String,Void,List<StopInfo>> {

	private static final String TAG = "FindStopsByLocationAsyncTask";
	private static final int SEARCH_DIAMETER_METERS = 500;
	
	
	private String user;
	private String password;
	private String serviceUrl;
	private StopUpdateListener listener;
	
	public FindStopsByLocationAsyncTask(String serviceUrl, String user, String password, StopUpdateListener listener) {
		super();
		this.serviceUrl = serviceUrl;
		this.user = user;
		this.password = password;
		this.listener = listener;
	}

	@Override 
	public void onPreExecute() {
		listener.backgroundTaskStarted();
	}
	
	
	@Override
	protected List<StopInfo> doInBackground(String... params) {
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?user={0}&pass={1}&request={2}&epsg_in=wgs84&format=json&center_coordinate={3},{4}&diameter={5}");

		String[] args = {user, password, "stops_area", "24.88083", "60.19701", String.valueOf(SEARCH_DIAMETER_METERS)};		

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl.format(args));

		Log.d(TAG, baseUrl.format(args));
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		List<StopInfo> stops = new ArrayList<StopInfo>();
		
		try {
			String responseString = httpClient.execute(request, responseHandler);
			JSONArray response = new JSONArray(responseString);
			
			for(int i = 0; i < response.length(); i ++) {
				String stopCode = response.getJSONObject(i).getString("code");
				stops.add(new StopInfo(stopCode, getPassingLines(stopCode)));
			}
			
		} catch(Exception e) {
			Log.e(TAG, "find stops by location failed", e);
		}
		
		return stops;
	}
	
	private List<TransportLine> getPassingLines(String stopCode) throws Exception {
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?user={0}&pass={1}&request={2}&format=json&code={3}");

		String[] args = {user, password, "stop", stopCode};		

		Log.d(TAG, baseUrl.format(args));
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl.format(args));	
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseString = httpClient.execute(request, responseHandler);
		JSONObject stopJson = new JSONArray(responseString).getJSONObject(0);
		
		List<TransportLine> linesPassing = new ArrayList<TransportLine>();
		JSONArray linesPassingStopJson = stopJson.getJSONArray("lines");
		for(int i = 0; i <linesPassingStopJson.length(); i ++) {
			JSONObject lineJson = linesPassingStopJson.getJSONObject(i);
			TransportLine line = new TransportLine("TODO", 0, lineJson.toString());
			linesPassing.add(line);
		}
		
		return linesPassing;
	}
	
	
	@Override
	public void onPostExecute(List<StopInfo> result) {
		listener.backgroundTaskEnded();
		Log.d(TAG, "Reporting found stops, number of stops: " + result.size());
		listener.transportLineStopsUpdate(new ArrayList<StopInfo>());
	}
	
	// reittiopas api wants coordinates in format lon/lat
	// api.reittiopas.fi/hsl/prod/?request=stops_area&epsg_in=wgs84&center_coordinate=24.88083,60.19701
	
}
