package com.hypermurea.hslpushdroid.reittiopas;

import com.hypermurea.hslpushdroid.TaskResultListener;
import java.util.List;
import android.os.AsyncTask;

public class FindLinesByNameAsyncTask extends AsyncTask<String,Void,List<TransportLine>> {

	private ReittiopasService service;

	private TaskResultListener<List<TransportLine>> listener;

	public FindLinesByNameAsyncTask(ReittiopasService service, TaskResultListener<List<TransportLine>> resultListener) {
		this.service = service;
		this.listener = resultListener;
	}

	@Override
	public void onPreExecute() {
		listener.backgroundTaskStarted();
	}

	@Override
	public List<TransportLine> doInBackground(String... searchTerms) {
		return service.findTransportLinesByName(searchTerms);
	}

	@Override
	public void onPostExecute(List<TransportLine> result) {
		listener.receiveResults(result);
		listener.backgroundTaskStopped();
	}

}
