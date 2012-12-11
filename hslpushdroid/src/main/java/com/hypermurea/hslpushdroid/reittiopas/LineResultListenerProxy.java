package com.hypermurea.hslpushdroid.reittiopas;

import java.util.List;

import com.hypermurea.hslpushdroid.TaskResultListener;

public abstract class LineResultListenerProxy<T> implements TaskResultListener<T> {
	
	private TaskResultListener<List<TransportLine>> parent;
	
	public LineResultListenerProxy(TaskResultListener<List<TransportLine>> parent) {
		this.parent = parent;
	}
	
	@Override
	public void backgroundTaskStarted() {
		parent.backgroundTaskStarted();
	}
	
	@Override
	public void backgroundTaskStopped() {
		parent.backgroundTaskStopped();
	}
	
	

}
