package com.hypermurea.hslpushdroid.user;

import java.util.List;
import java.util.UUID;

import com.google.inject.Inject;
import com.hypermurea.hslpushdroid.BackgroundTaskListener;
import com.hypermurea.hslpushdroid.MainActivity;
import com.hypermurea.hslpushdroid.gcm.GCMRegistrationService;
import com.hypermurea.hslpushdroid.reittiopas.TransportLine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class UserProfileFactory {
	
	private static final String TAG = "UserProfileFactory";
	
	@Inject private GCMRegistrationService gcmRegistrationService;
	private String serviceUrl;
	
	private static UserProfile profile;
	
	public UserProfileFactory(GCMRegistrationService gcmRegistrationService, String serviceUrl) {
		this.gcmRegistrationService = gcmRegistrationService;
		this.serviceUrl = serviceUrl;
	}
	
	public UserProfile getUserProfile(MainActivity activity) {		
		if(profile == null) {
			SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
			
			UserProfileFactory.profile = new UserProfile();	
			UserProfileFactory.profile.uuid = getUuid(preferences);
			UserProfileFactory.profile.linesOfInterest = getTransportLines(preferences);
		
			userListensToGcmRegistration(activity);
			gcmRegistrationService.registerForGcmMessaging(activity);
		 }
		
		return profile;
	}
	
	public void signalChangeInLinesOfInterest(BackgroundTaskListener progressListener) {
		UserLoginAsyncTask task = new UserLoginAsyncTask(serviceUrl, progressListener, profile);
		task.execute(profile);
	}
	
	private static String getUuid(SharedPreferences preferences) {
		final String UUID_KEY = "uuid_key";

		String uuid = preferences.getString(UUID_KEY, null);
		if(uuid == null) {
			uuid = UUID.randomUUID().toString();
			Editor editor = preferences.edit();
			editor.putString(UUID_KEY, uuid);
			editor.commit();
			Log.d(TAG, "generated new uuid: " + uuid);
		}
		
		return uuid;
	}
	
	private List<TransportLine> getTransportLines(SharedPreferences preferences) {
		final String TRANSPORT_LINES_JSON = "transport_lines";
		String transportLinesJson = preferences.getString(TRANSPORT_LINES_JSON, "[]");
		return TransportLine.getTransportLines(transportLinesJson);
	}
	
	private void userListensToGcmRegistration(final MainActivity activity) {
		activity.registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context ctx, Intent intent) {
						profile.registrationId = intent.getExtras().getString(GCMRegistrationService.REGISTRATION_ID);
						UserLoginAsyncTask task = new UserLoginAsyncTask(serviceUrl, activity, profile);
						task.execute(profile); 	 
					}
				}, new IntentFilter(GCMRegistrationService.REGISTERED_ACTION));		
	}
	

}
