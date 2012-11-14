package com.hypermurea.hslpushdroid;

import java.util.List;

public interface UserSignalListener {

	void signalUserLoggedIn(List<String> linesOfInterest);
	void signalLoginFailed();
	
}
