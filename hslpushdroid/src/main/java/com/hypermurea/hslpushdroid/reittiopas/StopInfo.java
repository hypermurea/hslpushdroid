package com.hypermurea.hslpushdroid.reittiopas;

import java.util.ArrayList;
import java.util.List;

public class StopInfo {
	
	public String code;
	public List<TransportLine> passingLines;

	public StopInfo(String code, List<TransportLine> lines) {
		this.code = code;
		this.passingLines = lines;
	}

	
}
