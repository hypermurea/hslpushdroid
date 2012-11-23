package com.hypermurea.hslpushdroid;

import java.util.List;

public interface FindLinesResultListener extends BackgroundTaskListener {

	public void receiveFindLinesResult(List<TransportLine> lines);
	
}
