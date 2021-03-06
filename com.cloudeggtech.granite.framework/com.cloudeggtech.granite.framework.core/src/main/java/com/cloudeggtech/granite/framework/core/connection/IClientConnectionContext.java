package com.cloudeggtech.granite.framework.core.connection;


public interface IClientConnectionContext extends IConnectionContext {
	boolean write(Object message, boolean sync);
	boolean close(boolean sync);
	
	Object getConnectionId();
	String getRemoteIp();
	int getRemotePort();
	
	boolean isTlsSupported();
	boolean isTlsStarted();
	void startTls();
	
	String getLocalNodeId();
	String getStreamId();
}
