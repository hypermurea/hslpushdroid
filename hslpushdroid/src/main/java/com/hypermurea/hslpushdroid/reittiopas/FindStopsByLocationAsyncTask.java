package com.hypermurea.hslpushdroid.reittiopas;

import java.text.MessageFormat;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hypermurea.hslpushdroid.BackgroundTaskListener;

import android.os.AsyncTask;
import android.util.Log;

public class FindStopsByLocationAsyncTask extends AsyncTask<String,Void,List<TransportLine>> {

	private static final String TAG = "FindStopsByLocationAsyncTask";
	
	private String user;
	private String password;
	private String serviceUrl;
	private BackgroundTaskListener listener;
	
	public FindStopsByLocationAsyncTask(String serviceUrl, String user, String password, BackgroundTaskListener listener) {
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
	protected List<TransportLine> doInBackground(String... params) {
		MessageFormat baseUrl = 
				new MessageFormat(serviceUrl + "?user={0}&password={1}&request={2}&epsg_in=wgs84&center_coordinate={3},{4}");

		String[] args = {user, password, "stops_area", "24.88083", "60.19701"};		

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(baseUrl.format(args));

		Log.d(TAG, baseUrl.format(args));
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		List<TransportLine> searchResult = null;
		
		return searchResult;
	}
	
	@Override
	public void onPostExecute(List<TransportLine> result) {
		listener.backgroundTaskEnded();
	}

	
	
	// reittiopas api wants coordinates in format lon/lat
	// api.reittiopas.fi/hsl/prod/?request=stops_area&epsg_in=wgs84&center_coordinate=24.88083,60.19701
	
}
