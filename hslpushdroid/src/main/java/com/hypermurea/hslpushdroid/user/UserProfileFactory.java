package com.hypermurea.hslpushdroid.user;

import java.util.List;
import java.util.UUID;

import org.json.JSONException;

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
	
	private GCMRegistrationService gcmRegistrationService;
	private String serviceUrl;
	private SharedPreferences preferences;
	
	private static UserProfile profile;
	
	final String TRANSPORT_LINES_JSON_PREFERENCE = "transport_lines";
	
	public UserProfileFactory(GCMRegistrationService gcmRegistrationService, String serviceUrl, SharedPreferences preferences) {
		this.gcmRegistrationService = gcmRegistrationService;
		this.serviceUrl = serviceUrl;
		this.preferences = preferences;
	}
	
	public UserProfile getUserProfile(MainActivity activity) {		
		if(profile == null) {
			UserProfileFactory.profile = new UserProfile();	
			UserProfileFactory.profile.uuid = getUuid();
			UserProfileFactory.profile.linesOfInterest = getTransportLines();
		
			userListensToGcmRegistration(activity);
			gcmRegistrationService.registerForGcmMessaging(activity);
		 }
		
		return profile;
	}
	
	// TODO poor style to have no unified way to handle exceptions
	public void signalChangeInUser(MainActivity activity) throws JSONException {
		Editor editor = preferences.edit();
		editor.putString(TRANSPORT_LINES_JSON_PREFERENCE, TransportLine.getJsonArray(UserProfileFactory.profile.linesOfInterest).toString());
		editor.commit();
		
		UserLoginAsyncTask task = new UserLoginAsyncTask(serviceUrl, activity, UserProfileFactory.profile);
		task.execute(profile);
		
	}
	
	private String getUuid() {
		final String UUID_KEY = "uuid_key";

		String uuid = preferences.getString(UUID_KEY, null);
		if(uuid == null) {
			uuid = UUID.randomUUID().toString();
			Editor editor = preferences.edit();
			editor.putString(UUID_KEY, uuid);
			editor.commit();
			Log.d(TAG, "generated new uuid: " + uuid);
		} else {
			Log.d(TAG, "UUID retrieved from preferences: " + uuid);
		}
		
		return uuid;
	}
	
	private List<TransportLine> getTransportLines() {
		String transportLinesJson = preferences.getString(TRANSPORT_LINES_JSON_PREFERENCE, "[]");
		if(transportLinesJson.equals("[]")) {
			Log.d(TAG, "transportLines not stored, defaulting");
		} else {
			Log.d(TAG, "transportLines loaded from user preferences");
		}
		return TransportLine.getTransportLines(transportLinesJson);
	}
	
	private void userListensToGcmRegistration(final MainActivity activity) {
		activity.registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context ctx, Intent intent) {
						activity.unregisterReceiver(this);
						UserProfileFactory.profile.registrationId = intent.getExtras().getString(GCMRegistrationService.REGISTRATION_ID);
						UserLoginAsyncTask task = new UserLoginAsyncTask(serviceUrl, activity, UserProfileFactory.profile);
						task.execute(UserProfileFactory.profile); 	 
					}
				}, new IntentFilter(GCMRegistrationService.REGISTERED_ACTION));		
	}
	

}
