package com.hypermurea.hslpushdroid;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationUpdateAgent implements LocationListener {
	
	private final static String TAG = "LocationUpdateAgent";
	
	public static final String LOCATION_UPDATE = "com.hypermurea.hslpushdroid.locationupdate";
	
	private final LocationManager locationManager;

	public LocationUpdateAgent(Context ctx) {
		locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean isLocationProviderAvailable() {
		return (getBestProvider() != null);
	}
	
	public boolean isLocationProviderEnabled() {
		return isLocationProviderAvailable() && locationManager.isProviderEnabled(getBestProvider());
	}
	
	public void startLocationUpdates() {
		locationManager.requestLocationUpdates(getBestProvider(), 10000, 10, this);
	}
	
	private String getBestProvider() {
		/**
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setBearingAccuracy(Criteria.NO_REQUIREMENT);
		
		return locationManager.getBestProvider(criteria, true);
		*/
		return LocationManager.GPS_PROVIDER;
	}
	
	public void stopLocationUpdates() {
		locationManager.removeUpdates(this);
	}


	@Override
	public void onLocationChanged(Location location) {
		Log.e(TAG, "location obtained (" + location.getProvider() + "), lat:" + location.getLatitude() + ", long:" + location.getLongitude());		
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
	
	/**
	 * 
	 * protected BroadcastReceiver singleUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive");
//unregister the receiver so that the application does not keep listening the broadcast even after the broadcast is received.
        context.unregisterReceiver(singleUpdateReceiver);
// get the location from the intent send in broadcast using the key - this step is very very important
        String key = LocationManager.KEY_LOCATION_CHANGED;
        Location location = (Location)intent.getExtras().get(key);
	 */

}
