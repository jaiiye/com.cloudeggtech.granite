package com.cloudeggtech.granite.cluster.integration.ignite.config;

public class SessionStorage extends Storage {
	public static final String NAME_SESSION_STORAGE = "session-storage";
	
	private int sessionDurationTime;
	
	public SessionStorage() {
		initSize = 16 * 1024 * 1024;
		maxSize = 64 * 1024 * 1024;
		persistenceEnabled = true;
		sessionDurationTime = 5 * 60;
	}

	public int getSessionDurationTime() {
		return sessionDurationTime;
	}

	public void setSessionDurationTime(int sessionDurationTime) {
		this.sessionDurationTime = sessionDurationTime;
	}
	
}
