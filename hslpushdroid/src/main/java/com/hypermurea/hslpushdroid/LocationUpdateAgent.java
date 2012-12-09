package com.hypermurea.hslpushdroid;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationUpdateAgent {
	
	private final static String TAG = "LocationUpdateAgent";
	
	public static final String LOCATION_UPDATE = "com.hypermurea.hslpushdroid.locationupdate";
	
	private final LocationManager locationManager;

	public LocationUpdateAgent(Context ctx) {
		locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean isLocationProviderEnabled() {
		return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
				locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
						
	}
	
	public void startLocationUpdates(LocationListener listener) {
		final int MINIMUM_DISTANCE_BETWEEN_LOCATION_UPDATES = 10;
		final int MINIMUM_TIME_BETWEEN_LOCATION_UPDATES = 2000;
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				MINIMUM_TIME_BETWEEN_LOCATION_UPDATES, 
				MINIMUM_DISTANCE_BETWEEN_LOCATION_UPDATES, 
				listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				MINIMUM_TIME_BETWEEN_LOCATION_UPDATES, 
				MINIMUM_DISTANCE_BETWEEN_LOCATION_UPDATES, 
				listener);
	}
		
	public void stopLocationUpdates(LocationListener listener) {
		locationManager.removeUpdates(listener);
	}

}
