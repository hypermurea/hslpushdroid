package com.hypermurea.hslpushdroid;
import java.util.List;

import com.hypermurea.hslpushdroid.TransportLine;


public class UserProfile implements UserSignalListener {
	
	public String uuid;
	public String registrationId;
	public List<TransportLine> linesOfInterest;
	
	private boolean latestUserStateCommitted = false;
	
	@Override
	public void signalUserLoggedIn() {
		latestUserStateCommitted = true;
	}
	
	@Override
	public void signalLoginFailed() {
		latestUserStateCommitted = false;
	}
	
}
