package com.hypermurea.hslpushdroid.reittiopas;

public interface FindLinesService {
	
	public void findLinesByName(FindLinesResultListener resultListener, String... query);
	public void startFindingLinesByLocation(FindLinesResultListener resultListener);
	public void stopFindingLinesByLocation();

}
