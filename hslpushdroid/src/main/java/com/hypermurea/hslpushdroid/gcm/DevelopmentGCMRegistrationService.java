package com.hypermurea.hslpushdroid.gcm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DevelopmentGCMRegistrationService implements GCMRegistrationService {
	
	private final static String TAG = "DevelopmentGCMRegistrationService";
	
	public void registerForGcmMessaging(Context ctx) {
		Log.e(TAG, "onRegistered invoked");
		
		final String DUMMY_GCM_REGISTRATION_ID = "dummy-reg-id";
		Intent intent = new Intent();
		intent.setAction(GCMRegistrationService.REGISTERED_ACTION);
		intent.putExtra(GCMRegistrationService.REGISTRATION_ID, DUMMY_GCM_REGISTRATION_ID);
		
		Log.e(TAG, "broadcasting acquisition of dummy registrationId: " + DUMMY_GCM_REGISTRATION_ID);
		ctx.sendBroadcast(intent);
	}
}
