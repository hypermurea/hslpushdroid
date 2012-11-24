package com.hypermurea.hslpushdroid.reittiopas;

import java.util.List;

import com.hypermurea.hslpushdroid.BackgroundTaskListener;

public interface FindLinesResultListener extends BackgroundTaskListener {

	public void receiveFindLinesResult(List<TransportLine> lines);
	
}
