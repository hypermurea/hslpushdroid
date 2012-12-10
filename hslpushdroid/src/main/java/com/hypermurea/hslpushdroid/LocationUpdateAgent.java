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
		if(!requestLocationUpdatesIfProviderEnabled(LocationManager.NETWORK_PROVIDER, listener)) {
			requestLocationUpdatesIfProviderEnabled(LocationManager.GPS_PROVIDER, listener);
		}
	}
	
	private boolean requestLocationUpdatesIfProviderEnabled(String provider, LocationListener listener) {
		final int MINIMUM_DISTANCE_BETWEEN_LOCATION_UPDATES = 10;
		final int MINIMUM_TIME_BETWEEN_LOCATION_UPDATES = 2000;
		if(locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider, 
					MINIMUM_TIME_BETWEEN_LOCATION_UPDATES, 
					MINIMUM_DISTANCE_BETWEEN_LOCATION_UPDATES, 
					listener);
			return true;
		} else {
			return false;
		}
	}
		
	public void stopLocationUpdates(LocationListener listener) {
		locationManager.removeUpdates(listener);
	}

}
