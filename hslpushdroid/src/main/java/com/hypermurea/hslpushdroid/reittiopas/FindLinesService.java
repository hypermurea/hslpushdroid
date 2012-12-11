package com.hypermurea.hslpushdroid.reittiopas;

import java.util.List;

import com.hypermurea.hslpushdroid.TaskResultListener;

public interface FindLinesService {
	
	public void findLinesByName(TaskResultListener<List<TransportLine>> resultListener, String... query);
	public void startFindingLinesByLocation(TaskResultListener<List<TransportLine>> resultListener);
	public void stopFindingLinesByLocation();

}
