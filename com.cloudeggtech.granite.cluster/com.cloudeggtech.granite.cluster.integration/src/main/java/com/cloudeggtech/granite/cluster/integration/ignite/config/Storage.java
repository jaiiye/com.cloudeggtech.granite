package com.cloudeggtech.granite.cluster.integration.ignite.config;

public class Storage {
	protected long initSize;
	protected long maxSize;
	protected boolean persistenceEnabled;
	
	public long getInitSize() {
		return initSize;
	}
	
	public void setInitSize(long initSize) {
		this.initSize = initSize;
	}
	
	public long getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isPersistenceEnabled() {
		return persistenceEnabled;
	}

	public void setPersistenceEnabled(boolean persistenceEnabled) {
		this.persistenceEnabled = persistenceEnabled;
	}
	
}
