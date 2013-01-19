package com.hypermurea.hslpushdroid.reittiopas;

import java.util.Set;
import com.hypermurea.hslpushdroid.TaskResultListener;
import android.location.Location;
import android.os.AsyncTask;

public class FindStopsByLocationAsyncTask extends AsyncTask<Location,Void,Set<String>> {

	private ReittiopasService service;

	private TaskResultListener<Set<String>> listener;
	
	public FindStopsByLocationAsyncTask(ReittiopasService service, TaskResultListener<Set<String>> listener) {
		super();
		this.service = service;
		this.listener = listener;
	}

	@Override 
	public void onPreExecute() {
		listener.backgroundTaskStarted();
	}

	@Override
	protected Set<String> doInBackground(Location... location) {
		return service.findStopsByLocation(location[0]);
	}

	@Override
	public void onPostExecute(Set<String> result) {
		listener.receiveResults(result);
		listener.backgroundTaskStopped();
	}

}
