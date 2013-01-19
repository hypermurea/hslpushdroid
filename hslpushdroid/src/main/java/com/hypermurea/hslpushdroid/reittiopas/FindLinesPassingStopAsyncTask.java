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

	private ReittiopasService service;

	private TaskResultListener<Set<String>> listener;

	public FindLinesPassingStopAsyncTask(ReittiopasService service, TaskResultListener<Set<String>> listener) {
		this.service = service;
		this.listener = listener;
	}

	@Override
	public void onPreExecute() {
		listener.backgroundTaskStarted();
	}
	
	@Override
	public Set<String> doInBackground(String... stopCode) {
		return service.findLinesPassingStop(stopCode[0]);
	}


	@Override
	public void onPostExecute(Set<String> result) {
		listener.receiveResults(result);
		listener.backgroundTaskStopped();
	}

}
