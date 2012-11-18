package com.hypermurea.hslpushdroid;

public class TransportLine {
	
	public String shortCode;
	public int transportType;
	public String name;	
	
	public TransportLine(String shortCode, int transportType, String name) {
		this.shortCode = shortCode;
		this.transportType = transportType;
		this.name = name;
	}
	
	// TODO remove ugly
	public String toString() {
		return this.shortCode + " " + name;
	}

}
