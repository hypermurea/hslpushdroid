package com.hypermurea.hslpushdroid.reittiopas;

import java.util.ArrayList;
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
	
	private String user;
	private String password;
	private String serviceUrl;
	private LocationUpdateAgent locationUpdateAgent;
	private TaskResultListener<List<TransportLine>> locationResultListener;

	private LineCache cache = new LineCache();

	private HashSet<String> queried = new HashSet<String>();

	public FindLinesServiceImpl(String serviceUrl, String user, String password, LocationUpdateAgent locationUpdateAgent) {
		this.serviceUrl = serviceUrl;
		this.user = user;
		this.password = password;
		this.locationUpdateAgent = locationUpdateAgent;
	}

	@Override
	public void findLinesByName(final TaskResultListener<List<TransportLine>> resultListener, String... query) {
		
		
		Log.d(TAG, "findLinesByName");
		Set<TransportLine> cachedResults = new HashSet<TransportLine>();
		Set<String> refinedQuery = new HashSet<String>();
		for(String queryString : query) {
			String cleanQueryString = TransportLine.cleanLineCode(queryString);
			TransportLine line = cache.getTransportLine(cleanQueryString);
			if(line != null) {
				cachedResults.add(line);
			} else {
				if(!queried.contains(cleanQueryString)) {
					queried.add(cleanQueryString);
					refinedQuery.add(queryString);
				}
			}
		}
		
		Log.d(TAG, "refined query: " + refinedQuery + ", size: " + refinedQuery.size());
		Log.d(TAG, "cached results already: " + cachedResults.size());

		if(!cachedResults.isEmpty()) {
			resultListener.receiveResults(new ArrayList<TransportLine>(cachedResults));
		}
		
		String[] refinedLines = refinedQuery.toArray(new String[refinedQuery.size()]);
		// Make sure the query is at least somewhat meaningful
		if(refinedLines.length > 0 && refinedLines[0].length() > 0) {

			FindLinesByNameAsyncTask task = new FindLinesByNameAsyncTask(serviceUrl, user, password, 
					new LineResultListenerProxy<List<TransportLine>>(resultListener) {

				@Override
				public void receiveResults(List<TransportLine> result) {
					for(TransportLine line : result) {
						cache.addTransportLine(line);
					}

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
		FindStopsByLocationAsyncTask task = new FindStopsByLocationAsyncTask(serviceUrl, user, password, 
				new LineResultListenerProxy<Set<String>>(locationResultListener) {

			@Override
			public void receiveResults(Set<String> result) {
				for(String stopCode: result) {
					findLinesPassingStop(stopCode, new LineResultListenerProxy<Set<String>>(locationResultListener) {

						@Override
						public void receiveResults(Set<String> result) {
							findLinesByName(locationResultListener, result.toArray(new String[result.size()]));
						}

					});
				}
			}
		});
		task.execute(location);		
	}

	private void findLinesPassingStop(String stopCode, TaskResultListener<Set<String>> resultListener) {
		// TODO run through cache, if no hit then user task
		FindLinesPassingStopAsyncTask task = new FindLinesPassingStopAsyncTask(serviceUrl, user, password, resultListener);
		task.execute(stopCode);
	}



}
