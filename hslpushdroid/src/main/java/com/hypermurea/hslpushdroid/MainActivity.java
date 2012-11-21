package com.hypermurea.hslpushdroid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

@SuppressLint("NewApi")
public class MainActivity extends RoboActivity implements FindLinesResultListener, TransportLineClickListener {

	private static String TAG = "hslpushdroid";

	@InjectView(R.id.mainLinearLayout)
	private LinearLayout mainLayout;
	
	@InjectView(R.id.linesListView) 
	private ListView linesListView;
	
	@Inject private FindLinesByNameAsyncTask findLinesTask;
	@Inject private UserProfileFactory userProfileFactory;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		injectDynamicLinesOfInterestViewsToLayout();
		updateDynamicLinesOfInterestViews(userProfileFactory.getUserProfile(this));
		
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);

			findLinesTask.setFindLinesResultListener(this);
			findLinesTask.execute(query);

		}
		
	}
	
	private void injectDynamicLinesOfInterestViewsToLayout() {
		if(mainLayout.findViewById(R.id.bussesOfInterest) == null) {
			addLineOfInterestRowToLayout(R.id.bussesOfInterest, R.drawable.bussi);
			addLineOfInterestRowToLayout(R.id.metrosOfInterest, R.drawable.metro);
			addLineOfInterestRowToLayout(R.id.tramsOfInterest, R.drawable.ratikka);
			addLineOfInterestRowToLayout(R.id.ferriesOfInterest, R.drawable.lautta);
		}
	}
	
	private void addLineOfInterestRowToLayout(int viewId, int drawableId) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.lines_of_interest, null);
		view.setId(viewId);
		ImageView imageView = (ImageView) view.findViewById(R.id.lineTypeImageView);
		imageView.setImageResource(drawableId);
	
		mainLayout.addView(view, 1);
	}

	
	private void updateDynamicLinesOfInterestViews(UserProfile profile) {
		updateDynamicRowOfInterestView(R.id.bussesOfInterest, R.drawable.bussi, profile);
		updateDynamicRowOfInterestView(R.id.metrosOfInterest, R.drawable.metro, profile);
		updateDynamicRowOfInterestView(R.id.tramsOfInterest, R.drawable.ratikka, profile);
		updateDynamicRowOfInterestView(R.id.ferriesOfInterest, R.drawable.lautta, profile);
	}
	
	private void updateDynamicRowOfInterestView(int viewId, int drawableId, UserProfile profile) {
		View view = mainLayout.findViewById(viewId);
		TextView textView = (TextView) view.findViewById(R.id.linesOfInterestTextView);
		String lineCodesConcatenated = "";
		for(TransportLine line : profile.linesOfInterest) {
			if(TransportLineAdapter.getDrawableId(line) == drawableId) {
				lineCodesConcatenated += line.shortCode + " ";
			}
		}
		
		textView.setText(lineCodesConcatenated);
		if(lineCodesConcatenated.length() > 0) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
				
	}	

	@Override
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
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return false;
        }
    }

	@Override
	public void transportLineClicked(TransportLine line) {
		Log.d(TAG, "transportLineClicked: " + line.shortCode + " " + line.transportType);
		
		TransportLineAdapter listAdapter = (TransportLineAdapter) linesListView.getAdapter();
		listAdapter.remove(line);
		listAdapter.notifyDataSetChanged();
		
		userProfileFactory.getUserProfile(this).linesOfInterest.add(line);
		updateDynamicLinesOfInterestViews(userProfileFactory.getUserProfile(this));
		userProfileFactory.signalChangeInLinesOfInterest();
		
		Log.d(TAG, "reached");
	
	}
	
	@Override
	public void receiveFindLinesResult(List<TransportLine> lines) {
		
		if(lines != null) {
			TransportLineAdapter adapter = new TransportLineAdapter(this, R.layout.line_list_row, lines, this);
			linesListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();			
		} else {
			Toast.makeText(this, "search failed", Toast.LENGTH_LONG).show();
		}
	}


}
