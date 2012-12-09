package com.hypermurea.hslpushdroid.reittiopas;

import java.util.HashMap;
import java.util.Set;

public class LineCache {
	
	private HashMap<String, TransportLine> linesById = new HashMap<String, TransportLine>();
	private HashMap<String, Set<String>> linesByStop = new HashMap<String, Set<String>>();
	
	public TransportLine getTransportLine(String id) {
		return linesById.get(id);
	}

	public void addTransportLine(TransportLine line) {
		for(String code : line.codes) {
			linesById.put(code,  line);
		}
	}
	
	public Set<String> getLineCodesPassingStop(String stopCode) {
		return linesByStop.get(stopCode);
	}
	
	public void setLineCodesPassingStop(String stopCode, Set<String> lineCodes) {
		linesByStop.put(stopCode, lineCodes);
	}
	
}
