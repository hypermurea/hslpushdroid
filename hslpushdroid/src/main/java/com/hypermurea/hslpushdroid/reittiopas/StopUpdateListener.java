package com.hypermurea.hslpushdroid.reittiopas;

import java.util.List;

import com.hypermurea.hslpushdroid.BackgroundTaskListener;

public interface StopUpdateListener extends BackgroundTaskListener {

	void transportLineStopsUpdate(List<StopInfo> stops);
	
}
