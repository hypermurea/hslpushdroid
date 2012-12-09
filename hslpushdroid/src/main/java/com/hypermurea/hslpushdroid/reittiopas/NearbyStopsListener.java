package com.hypermurea.hslpushdroid.reittiopas;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.hypermurea.hslpushdroid.LocationUpdateAgent;

public class NearbyStopsListener implements LocationListener {

	private static final String TAG = "NearbyStopsListener";
	
	private LocationUpdateAgent locationUpdateAgent;
	
	private String serviceUrl;
	private String user;
	private String password;
	
	private FindLinesResultListener listener;
	// TODO implement to avoid querying for the same stop data over and over again
	//private HashMap<String,StopInfo> stopCache = new HashMap<String, StopInfo>();
	
	public NearbyStopsListener(LocationUpdateAgent locationUpdateAgent, String serviceUrl, String user, String password) {
		this.locationUpdateAgent = locationUpdateAgent;
		this.serviceUrl = serviceUrl;
		this.user = user;
		this.password = password;
	}
	
	public void startListeningToStopUpdates(FindLinesResultListener listener) {
		this.listener = listener;
		locationUpdateAgent.startLocationUpdates(this);
	}
	
	public void stopListeningToStopUpdates() {
		locationUpdateAgent.stopLocationUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged");
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
	

}
