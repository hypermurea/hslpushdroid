package com.hypermurea.hslpushdroid.reittiopas;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hypermurea.hslpushdroid.LocationUpdateAgent;
import com.hypermurea.hslpushdroid.TaskResultListener;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class FindLinesServiceImpl implements FindLinesService, LocationListener {

	private static final String TAG = "FindLinesServiceImpl";
	
	private ReittiopasService service;
	private LocationUpdateAgent locationUpdateAgent;
	private TaskResultListener<List<TransportLine>> locationResultListener;

	public FindLinesServiceImpl(ReittiopasService service, LocationUpdateAgent locationUpdateAgent) {
		this.service = service;
		this.locationUpdateAgent = locationUpdateAgent;
	}

	@Override
	public void findLinesByName(final TaskResultListener<List<TransportLine>> resultListener, String... query) {
		
		Set<String> refinedQuery = new HashSet<String>();
		for(String queryString : query) {
			refinedQuery.add(queryString);
		}
		
		Log.d(TAG, "refined query: " + refinedQuery + ", size: " + refinedQuery.size());
		
		String[] refinedLines = refinedQuery.toArray(new String[refinedQuery.size()]);
		// Make sure the query is at least somewhat meaningful
		if(refinedLines.length > 0 && refinedLines[0].length() > 0) {

			FindLinesByNameAsyncTask task = new FindLinesByNameAsyncTask(service, 
					new LineResultListenerProxy<List<TransportLine>>(resultListener) {

				@Override
				public void receiveResults(List<TransportLine> result) {
					resultListener.receiveResults(result);
				}

			});
			
			task.execute(refinedLines);

		}

	}

	@Override
	public void startFindingLinesByLocation(TaskResultListener<List<TransportLine>> resultListener) {
		locationResultListener = resultListener;
		resultListener.backgroundTaskStarted();
		locationUpdateAgent.startLocationUpdates(this);
	}

	@Override
	public void stopFindingLinesByLocation() {
		locationUpdateAgent.stopLocationUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onLocationChanged(Location location) {
		locationUpdateAgent.stopLocationUpdates(this);
		locationResultListener.backgroundTaskStopped();
		findStopsByLocation(location);
	}

	private void findStopsByLocation(Location location) {

		FindLinesByLocationAsyncTask task = new FindLinesByLocationAsyncTask(service, 
				new LineResultListenerProxy<List<TransportLine>>(locationResultListener) {

			@Override
			public void receiveResults(List<TransportLine> results) {
				locationResultListener.receiveResults(results);
			}
		});
		task.execute(location);		
	}

	private void findLinesPassingStop(String stopCode, TaskResultListener<Set<String>> resultListener) {
		// TODO run through cache, if no hit then user task
		FindLinesPassingStopAsyncTask task = new FindLinesPassingStopAsyncTask(service, resultListener);
		task.execute(stopCode);
	}



}
