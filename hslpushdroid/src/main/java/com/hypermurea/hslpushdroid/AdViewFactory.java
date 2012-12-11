package com.hypermurea.hslpushdroid;

import android.app.Activity;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdViewFactory {
	
	private String publisherId;
	private String[] testDevices;
	
	public AdViewFactory(String publisherId, String... testDevices) {
		this.publisherId = publisherId;
		this.testDevices = testDevices;
	}
	
	public AdView getAdView(Activity activity, LinearLayout addToLayout) {
		return new AdView(activity, AdSize.BANNER, publisherId);	
	}
	
	public AdRequest getAdRequest() {
		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		for(String testDevice : testDevices) {
			adRequest.addTestDevice(testDevice);
		}
		return adRequest;
	}

}
