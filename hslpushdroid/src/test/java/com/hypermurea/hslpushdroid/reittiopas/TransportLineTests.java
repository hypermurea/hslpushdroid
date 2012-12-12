package com.hypermurea.hslpushdroid.reittiopas;

import junit.framework.TestCase;

public class TransportLineTests extends TestCase {
	
	public void testSpacesInLineCodeAreRemoved() {
		TransportLine twospaces = new TransportLine("shortcode", "1234X 2", 1, "testline");
		TransportLine trailingspaces = new TransportLine("shortcode", "1235X   ", 1, "testline");
		
		assertEquals(twospaces.code, "1234X");
		assertEquals(trailingspaces.code, "1235X");
	}

}
