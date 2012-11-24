package com.hypermurea.hslpushdroid.reittiopas;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.os.AsyncTask;
import android.util.Log;

public class FindLinesByNameAsyncTask extends AsyncTask<String,Void,List<TransportLine>>{

	private static final String TAG = "FindLinesByNameAsyncTask";

	private String serviceUrl;
	private String user;
	private String password;

	private FindLinesResultListener listener;

	public FindLinesByNameAsyncTask(String serviceUrl, String user, String password) {
		super();
		this.serviceUrl = serviceUrl;
		this.user = user;
		this.password = password;
	}

	@Override 
	public void onPreExecute() {
		listener.backgroundTaskStarted();
	}
	
	@Override
	protected List<TransportLine> doInBackground(String... searchTerms) {
		
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?request=lines&format=json&user={0}&pass={1}&query={2}");

		String[] args = {user, password, searchTerms[0]};		

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl.format(args));

		Log.d(TAG, baseUrl.format(args));
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		List<TransportLine> searchResult = null;

		try {
			String responseString = httpClient.execute(request, responseHandler);
			JSONArray response = new JSONArray(responseString);

			searchResult = new ArrayList<TransportLine>();
			HashMap<String, TransportLine> resultHash = new HashMap<String, TransportLine>();
			for( int i = 0; i < response.length(); i ++) {
				JSONObject lineJson = response.getJSONObject(i);
				TransportLine line = new TransportLine(lineJson.getString("code_short"), lineJson.getInt("transport_type_id"), lineJson.getString("name"));
				String key = line.shortCode + " " + line.transportType;
				if(!resultHash.containsKey(key)) {
					resultHash.put(key, line);
					searchResult.add(line);
				} 
				resultHash.get(key).codes.add(lineJson.getString("code"));
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

	@Override
	public void onPostExecute(List<TransportLine> result) {
		listener.backgroundTaskEnded();
		listener.receiveFindLinesResult(result);
	}

	public void setFindLinesResultListener(FindLinesResultListener listener) {
		this.listener = listener;
	}

}
