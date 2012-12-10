package com.hypermurea.hslpushdroid.reittiopas;

import java.util.Set;

public interface FindStopsResultListener {

	public void receiveStops(Set<String> stopCodes);
	public void receiveLineCodes(Set<String> lineCodes);
	
}
