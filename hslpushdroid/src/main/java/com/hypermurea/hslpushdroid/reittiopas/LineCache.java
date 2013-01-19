package com.hypermurea.hslpushdroid.reittiopas;

import java.util.HashMap;

// TODO not needed right now, so could make good sense to remove
public class LineCache {
	
	private HashMap<String, TransportLine> linesById = new HashMap<String, TransportLine>();
	
	public TransportLine getTransportLine(String id) {
		return linesById.get(id);
	}

	public void addTransportLine(TransportLine line) {
		linesById.put(line.shortCode + "_" + line.transportType, line);
	}
	
}
