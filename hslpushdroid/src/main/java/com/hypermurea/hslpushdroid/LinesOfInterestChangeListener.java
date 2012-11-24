package com.hypermurea.hslpushdroid;

import com.hypermurea.hslpushdroid.reittiopas.TransportLine;

public interface LinesOfInterestChangeListener {

	public void addTransportLine(TransportLine line);
	public void removeTransportLine(TransportLine line);
	
}
