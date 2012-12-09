package com.hypermurea.hslpushdroid.reittiopas;

import java.util.Set;

public interface FindStopsResultListener {

	// TODO Maybe it should actually be that the reittiopas api is accessed through an interface, of which
	// the implemention facilitates caching. 
	
	public void receiveStops(Set<String> stopCodes);
	public void receiveLineCodes(Set<String> lineCodes);
	
}
