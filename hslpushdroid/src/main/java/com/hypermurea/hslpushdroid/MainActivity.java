package com.hypermurea.hslpushdroid;

import java.util.List;
import java.util.UUID;

import com.google.android.gcm.GCMRegistrar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class MainActivity extends RoboActivity implements UserSignalListener {

	private static String TAG = "hslpushdroid";

	@InjectView(R.id.linesOfInterestListView)
	private ListView linesOfInterestListView;
	
	/**
	 * Called when the activity is first created.
	 * @param savedInstanceState If the activity is being re-initialized after 
	 * previously being shut down then this Bundle contains the data it most 
	 * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.registerReceiver(
				new BroadcastReceiver() {
					 @Override
					 public void onReceive(Context ctx, Intent intent) {
						 
						UserLoginAsyncTask loginTask = new UserLoginAsyncTask(MainActivity.this, getUniqueUserIdentifier(), intent.getStringExtra(GCMIntentService.REGISTRATION_ID));
						loginTask.execute((Void[])null);			 

					 }
				}, new IntentFilter(GCMIntentService.REGISTERED_ACTION));
		
		
		registerForGcmMessaging();

		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);
	}
	
	private String getUniqueUserIdentifier() {
		SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
		final String UUID_KEY = "uuid_key";
		
		String uuid = preferences.getString(UUID_KEY, null);
		
		if(uuid == null) {
			uuid = UUID.randomUUID().toString();
			Editor editor = preferences.edit();
			editor.putString(UUID_KEY, uuid);
			editor.commit();
			Log.d(TAG, "generated new uuid: " + uuid);
		}
		
		Log.d(TAG, "uuid request response: " + uuid);
		return uuid;
	}

	private void registerForGcmMessaging() {
		GCMRegistrar.checkDevice(this);
		// TODO Uncomment when ready for release
		//GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			final String SENDER_ID = "956341386030";
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			Log.v(TAG, "Already registered, regId: " +  regId);
			UserLoginAsyncTask loginTask = new UserLoginAsyncTask(MainActivity.this, getUniqueUserIdentifier(), regId);
			loginTask.execute((Void[])null);			 
		}
	}
	
	@Override
	public void signalUserLoggedIn(List<String> linesOfInterest) {
		Log.d(TAG, "signalUserLoggedIn");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, linesOfInterest);
		linesOfInterestListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		Toast.makeText(this, "User logged in", Toast.LENGTH_LONG).show();
	}

	@Override
	public void signalLoginFailed() {
		Log.d(TAG, "signalLoginFailed");
		
		Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
	}

}
