package com.hypermurea.hslpushdroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

public class UserLoginAsyncTask extends AsyncTask<UserProfile,Void,Boolean> {

	private final String TAG = "UserLoginAsyncTask";

	private String serviceUrl;
	private UserSignalListener[] listeners;
	
	public UserLoginAsyncTask(String serviceUrl, UserSignalListener... listeners) {
		this.serviceUrl = serviceUrl;
		this.listeners = listeners;
	}

	@Override
	protected Boolean doInBackground(UserProfile... profile) {

		HttpClient httpClient = new DefaultHttpClient();
		String query = "/gcm";
		HttpPost request = new HttpPost(serviceUrl + query);

		boolean loginSuccess = false;
		
		try {

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("uuid", profile[0].uuid));
			nameValuePairs.add(new BasicNameValuePair("regId", profile[0].registrationId));
			nameValuePairs.add(new BasicNameValuePair("lof", TransportLine.getJsonArray(profile[0].linesOfInterest).toString()));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			Log.d(TAG, "Invoking login from url: " + serviceUrl + query);
			
			Log.d(TAG, "Lines of interest: " + TransportLine.getJsonArray(profile[0].linesOfInterest).toString());
			BasicResponseHandler responseHandler = new BasicResponseHandler();

			String responseString = httpClient.execute(request, responseHandler);
			if(responseString.equals("OK")) {
				loginSuccess = true;
			} else {
				Log.e(TAG, "Login failed, response string: " + responseString);
			}


		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(JSONException e) {
			e.printStackTrace();
		}

		return loginSuccess;
	}

	@Override
	public void onPostExecute(Boolean result) {
		if(result) {
			for(UserSignalListener listener: listeners) {
				listener.signalUserLoggedIn();				
			}
		} else {
			for(UserSignalListener listener: listeners) {
				listener.signalLoginFailed();			
			}
		}
	}

}
