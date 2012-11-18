package com.hypermurea.hslpushdroid.gcm;

import android.content.Context;

import com.google.android.gcm.GCMRegistrar;

public class LiveGCMRegistrationService implements GCMRegistrationService {
	
	private final static String TAG = "GCMRegistrationService";
	
	private String senderId;
	
	public void registerForGcmMessaging(Context ctx) {
		GCMRegistrar.checkDevice(ctx);
		GCMRegistrar.checkManifest(ctx);
		final String regId = GCMRegistrar.getRegistrationId(ctx);
		if (regId.equals("")) {
			GCMRegistrar.register(ctx, senderId);
		} 
	}
	
	public void setSenderId(String s) {
		this.senderId = s;
	}
	
	/**
	 *  else {
			Log.v(TAG, "Already registered, regId: " +  regId);
			UserLoginAsyncTask loginTask = new UserLoginAsyncTask(MainActivity.this, regId, getUniqueUserIdentifier());
			loginTask.execute((Void[])null);			 
		}
	 */


}
