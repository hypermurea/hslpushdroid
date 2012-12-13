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

import com.hypermurea.hslpushdroid.TaskResultListener;

import android.os.AsyncTask;
import android.util.Log;

public class FindLinesPassingStopAsyncTask extends AsyncTask<String,Void,Set<String>> {

	private static final String TAG = "FindStopsByLocationAsyncTask";

	private String user;
	private String password;
	private String serviceUrl;

	private TaskResultListener<Set<String>> listener;

	public FindLinesPassingStopAsyncTask(String serviceUrl, String user, String password, TaskResultListener<Set<String>> listener) {
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
	public Set<String> doInBackground(String... stopCode) {

		Set<String> passingLineCodes = new HashSet<String>();

		try {
			MessageFormat baseUrl = 
					new MessageFormat(serviceUrl + "?user={0}&pass={1}&request={2}&format=json&code={3}&p=0000001");

			String[] args = {user, password, "stop", stopCode[0]};		

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


	@Override
	public void onPostExecute(Set<String> result) {
		listener.receiveResults(result);
		listener.backgroundTaskStopped();
	}

}
