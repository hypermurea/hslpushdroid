package com.hypermurea.hslpushdroid.reittiopas;

import java.util.Set;

import com.hypermurea.hslpushdroid.LocationUpdateAgent;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class FindLinesServiceImpl implements FindLinesService, LocationListener, TaskResultListener<Set<String>> {

	private String user;
	private String password;
	private String serviceUrl;
	private LocationUpdateAgent locationUpdateAgent;
	private FindLinesResultListener locationResultListener;
	
	private LineCache cache = new LineCache();
	
	public FindLinesServiceImpl(String serviceUrl, String user, String password, LocationUpdateAgent locationUpdateAgent) {
		this.serviceUrl = serviceUrl;
		this.user = user;
		this.password = password;
		this.locationUpdateAgent = locationUpdateAgent;
	}
	
	@Override
	public void findLinesByName(FindLinesResultListener resultListener, String... query) {
		// cache hit should be checked
		FindLinesByNameAsyncTask task = new FindLinesByNameAsyncTask(serviceUrl, user, password, resultListener);
		task.execute(query);
	}

	@Override
	public void startFindingLinesByLocation(
			FindLinesResultListener resultListener) {
		// if you wanted to make this not use mutable state (involving resultListener), this method would need
		// to create a new object/process that is created whenever location updates are desired
		locationResultListener = resultListener;
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
		findStopsByLocation(location);
		// step 1: find nearby stop codes
		// step 2: find lines for each stop code
		// step 3: find 
	}
	
	private void findStopsByLocation(Location location) {
		FindStopsByLocationAsyncTask task = new FindStopsByLocationAsyncTask(serviceUrl, user, password, 
				new TaskResultListener<Set<String>>() {

					@Override
					public void receiveResults(Set<String> result) {
						for(String stopCode: result) {
							findLinesPassingStop(stopCode, new TaskResultListener<Set<String>>() {

								@Override
								public void receiveResults(Set<String> result) {
									findLinesByName(locationResultListener, result.toArray(new String[result.size()]));
								}
								
							});
						}
					}
		});
		task.execute();		
	}
	
	private void findLinesPassingStop(String s, TaskResultListener<Set<String>> resultListener) {
		// TODO run through cache, if no hit then user task
		FindLinesPassingStopAsyncTask task = new FindLinesPassingStopAsyncTask(serviceUrl, user, password, resultListener);
		task.execute();
	}

	@Override
	public void receiveResults(Set<String> result) {
		
	}

}
