package com.cloudeggtech.granite.framework.core.integration;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.core.connection.IClientConnectionContext;
import com.cloudeggtech.granite.framework.core.connection.IConnectionManager;

public interface IClientMessageProcessor extends IMessageProcessor {
	void setConnectionManager(IConnectionManager connectionManager);
	void connectionOpened(IClientConnectionContext context);
	void connectionClosing(IClientConnectionContext context);
	void connectionClosed(IClientConnectionContext context, JabberId sessionJid);
}
