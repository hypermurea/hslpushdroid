package com.hypermurea.hslpushdroid.reittiopas;

import java.util.HashMap;

public class LineCache {
	
	private HashMap<String, TransportLine> linesById = new HashMap<String, TransportLine>();
	
	public TransportLine getTransportLine(String id) {
		return linesById.get(id);
	}

	public void addTransportLine(TransportLine line) {
		for(String code : line.codes) {
			linesById.put(code,  line);
		}
	}
	
}
