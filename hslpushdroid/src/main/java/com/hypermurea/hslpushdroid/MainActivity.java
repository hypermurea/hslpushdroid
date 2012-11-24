package com.hypermurea.hslpushdroid;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.hypermurea.hslpushdroid.reittiopas.FindLinesByNameAsyncTask;
import com.hypermurea.hslpushdroid.reittiopas.FindLinesResultListener;
import com.hypermurea.hslpushdroid.reittiopas.TransportLine;
import com.hypermurea.hslpushdroid.user.UserProfile;
import com.hypermurea.hslpushdroid.user.UserProfileFactory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

@SuppressLint("NewApi")
public class MainActivity extends RoboActivity implements FindLinesResultListener, LinesOfInterestChangeListener {

	private static String TAG = "hslpushdroid";

	@InjectView(R.id.mainLinearLayout)
	private LinearLayout mainLayout;
	
	@InjectView(R.id.linesListView) 
	private ListView linesListView;
	
	@Inject private FindLinesByNameAsyncTask findLinesTask;
	@Inject private UserProfileFactory userProfileFactory;
	
	private int backgroundTasksRunning = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.main);
		
		injectDynamicLinesOfInterestViewsToLayout();
		updateDynamicLinesOfInterestViews(userProfileFactory.getUserProfile(this));
		
		setProgressBarIndeterminateVisibility(false);

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
		Button linesOfInterestButton = (Button) view.findViewById(R.id.linesOfInterestButton);
		String lineCodesConcatenated = "";
		List<TransportLine> lines = new ArrayList<TransportLine>();
		for(TransportLine line : profile.linesOfInterest) {
			if(TransportLineAdapter.getDrawableId(line) == drawableId) {
				lineCodesConcatenated += line.shortCode + " ";
				lines.add(line);
			}
		}
		
		linesOfInterestButton.setText(lineCodesConcatenated);
		
		if(lineCodesConcatenated.length() > 0) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
		
		final ArrayAdapter<TransportLine> adapter = new ArrayAdapter<TransportLine>(this,
		        android.R.layout.simple_spinner_dropdown_item, lines);
		
		linesOfInterestButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				  new AlertDialog.Builder(MainActivity.this)
				  .setTitle("Poista seurattavia linjoja")
				  .setAdapter(adapter, new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface dialog, int which) {

				      ListView items = ((AlertDialog) dialog).getListView();
				      MainActivity.this.removeTransportLine((TransportLine) items.getAdapter().getItem(which));
				     
				      dialog.dismiss();
				    }
				  }).create().show();
				}
        });
		

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
	public void removeTransportLine(TransportLine line) {
		userProfileFactory.getUserProfile(this).linesOfInterest.remove(line);
		updateDynamicLinesOfInterestViews(userProfileFactory.getUserProfile(this));
		userProfileFactory.signalChangeInLinesOfInterest(this);
	}
	
	@Override
	public void addTransportLine(TransportLine line) {
		Log.d(TAG, "transportLineClicked: " + line.shortCode + " " + line.transportType);
		
		TransportLineAdapter listAdapter = (TransportLineAdapter) linesListView.getAdapter();
		listAdapter.remove(line);
		listAdapter.notifyDataSetChanged();
		
		userProfileFactory.getUserProfile(this).linesOfInterest.add(line);
		updateDynamicLinesOfInterestViews(userProfileFactory.getUserProfile(this));
		userProfileFactory.signalChangeInLinesOfInterest(this);
		
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
	
	@Override
	public void backgroundTaskStarted() {
		backgroundTasksRunning++;
		if(backgroundTasksRunning > 0) {
			synchronized(this) {
				if(backgroundTasksRunning > 0) {
					this.setProgressBarIndeterminateVisibility(true);					
				}
			}
		}

	}
	
	@Override
	public void backgroundTaskEnded() {
		backgroundTasksRunning--;
		if(backgroundTasksRunning == 0) {
			synchronized(this) {
				if(backgroundTasksRunning == 0) {
					this.setProgressBarIndeterminateVisibility(false);
				}
			}
		}
		
	}


}
