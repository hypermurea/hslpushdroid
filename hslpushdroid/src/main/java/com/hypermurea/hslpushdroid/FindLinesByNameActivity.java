package com.hypermurea.hslpushdroid;

import java.util.List;

import com.google.inject.Inject;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import roboguice.activity.RoboActivity;

public class FindLinesByNameActivity extends RoboActivity implements FindLinesResultListener {
 
	private static final String TAG = "FindLinesByNameActivity";
	
	@Inject 
	private FindLinesByNameAsyncTask findLinesTask;
	
	// TODO Perhaps merge this to same activity as in SearchableDictionary. Making a new view does not necessarily make sense.
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      
	      findLinesTask.setFindLinesResultListener(this);
	      findLinesTask.execute(query);
	      
	    }
	
	}

	@Override
	public void receiveFindLinesResult(List<String> lines) {
		// TODO Auto-generated method stub
		for(String s : lines) {
			Log.d(TAG, "line: " + s);
		}
	}
}
