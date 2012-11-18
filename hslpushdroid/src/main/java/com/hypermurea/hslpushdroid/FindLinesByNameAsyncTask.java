package com.hypermurea.hslpushdroid;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

public class FindLinesByNameAsyncTask extends AsyncTask<String,Void,List<String>>{

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
	protected List<String> doInBackground(String... searchTerms) {
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?request=lines&format=json&user={0}&pass={1}&query={2}");
		
		String[] args = {user, password, searchTerms[0]};		
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl.format(args));
		
		Log.d(TAG, baseUrl.format(args));
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		List<String> searchResult = null;
		
		try {
			String responseString = httpClient.execute(request, responseHandler);
			JSONArray response = new JSONArray(responseString);
			
			searchResult = new ArrayList<String>();
			for( int i = 0; i < response.length(); i ++) {
				searchResult.add(response.getJSONObject(i).get("code_short").toString());
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
	public void onPostExecute(List<String> result) {
		listener.receiveFindLinesResult(result);
	}
	
	public void setFindLinesResultListener(FindLinesResultListener listener) {
		this.listener = listener;
	}
	
}
