package com.hypermurea.hslpushdroid;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;

public class LocationUpdateAgent {
	
	public void getLocationUpdate(Context context, LocationListener listener) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setBearingAccuracy(Criteria.NO_REQUIREMENT);		
	}

}
