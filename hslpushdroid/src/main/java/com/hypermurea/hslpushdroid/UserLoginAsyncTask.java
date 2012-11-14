package com.hypermurea.hslpushdroid;

import java.io.IOException;
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

public class UserLoginAsyncTask extends AsyncTask<Void,Void,List<String>> {

	private final String TAG = "UserLoginAsyncTask";
	
	private String registrationId;
	private String uuid;
	
	private JSONArray response;
	
	private UserSignalListener listener;
	
	public UserLoginAsyncTask(UserSignalListener listener, String registrationId, String uuid) {
		this.listener = listener;
		this.uuid = uuid;
		this.registrationId = registrationId;
	}
	
	public List<String> getNotificationsOnLines() throws JSONException {
		ArrayList<String> lines = new ArrayList<String>();
		for( int i = 0; i < response.length(); i ++) {
			lines.add(response.getString(i));
		}
		return lines;
	}

	@Override
	protected List<String> doInBackground(Void... params) {
		String baseUrl = "http://hslpushtwo.elasticbeanstalk.com/gcm";
		String queryString = "?uuid=" + uuid + "&regId=" + registrationId; 

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl + queryString);
		
		Log.d(TAG, baseUrl+queryString);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		List<String> linesOfInterest = null;
		
		try {
			String responseString = httpClient.execute(request, responseHandler);
			response = new JSONArray(responseString);
			
			linesOfInterest = new ArrayList<String>();
			for( int i = 0; i < response.length(); i ++) {
				linesOfInterest.add(response.getString(i));
			}	
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return linesOfInterest;
	}
	
	@Override
	public void onPostExecute(List<String> result) {
		if(result != null) {
			listener.signalUserLoggedIn(result);
		} else {
			listener.signalLoginFailed();
		}
	}

}
