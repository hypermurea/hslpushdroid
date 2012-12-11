package com.hypermurea.hslpushdroid;


public interface TaskResultListener<T> extends BackgroundTaskListener {
	
	public void receiveResults(T result);
	
}
