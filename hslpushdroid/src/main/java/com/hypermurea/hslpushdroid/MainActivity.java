package com.hypermurea.hslpushdroid;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import com.hypermurea.hslpushdroid.gcm.GCMRegistrationService;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;

@SuppressLint("NewApi")
public class MainActivity extends RoboActivity implements UserSignalListener, FindLinesResultListener {

	private static String TAG = "hslpushdroid";

	@Inject
	private GCMRegistrationService gcmRegistrationService;

	@Inject
	private UserLoginAsyncTask loginTask;
	@Inject
	private FindLinesByNameAsyncTask findLinesTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		Log.e(TAG, "onCreate invoked");
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.e(TAG, "ACTION SEARCH INVOKED");
			String query = intent.getStringExtra(SearchManager.QUERY);

			findLinesTask.setFindLinesResultListener(this);
			findLinesTask.execute(query);

		} else { // TODO Probably would be better to check for LAUNCH 

			registerGcmRegistrationBroadcastReceiver();
			gcmRegistrationService.registerForGcmMessaging(this);

			setContentView(R.layout.main);

		}
	}

	private void registerGcmRegistrationBroadcastReceiver() {
		this.registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context ctx, Intent intent) {
						loginTask.setUserSignalListener(MainActivity.this);
						loginTask.execute(new String[] {getUniqueUserIdentifier(), intent.getStringExtra(GCMRegistrationService.REGISTRATION_ID)});	 
					}
				}, new IntentFilter(GCMRegistrationService.REGISTERED_ACTION));	
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


	@Override
	public void signalUserLoggedIn(List<String> linesOfInterest) {
		Log.d(TAG, "signalUserLoggedIn");

		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, linesOfInterest);
		//linesOfInterestListView.setAdapter(adapter);
		//adapter.notifyDataSetChanged();

		Toast.makeText(this, "User logged in", Toast.LENGTH_LONG).show();
	}

	@Override
	public void signalLoginFailed() {
		Log.d(TAG, "signalLoginFailed");

		Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
	}

	@Override
	public void receiveFindLinesResult(List<String> lines) {
		
		for(String s : lines) {
			Log.e(TAG, "Found line: " + s);
		}
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lines);
		//pickLinesListView.setAdapter(adapter);
		//adapter.notifyDataSetChanged();		
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

			SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
			searchView.setSearchableInfo(
					searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}
		return true;
	}

	public void setGCMRegistrationService(GCMRegistrationService service) {
		this.gcmRegistrationService = service;
	}


}
