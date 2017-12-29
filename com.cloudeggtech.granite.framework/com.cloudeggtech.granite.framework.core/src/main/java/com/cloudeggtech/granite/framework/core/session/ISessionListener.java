package com.cloudeggtech.granite.framework.core.session;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;

public interface ISessionListener {
	void sessionEstablishing(IConnectionContext context, JabberId sessionJid);
	void sessionEstablished(IConnectionContext context, JabberId sessionJid);
	void sessionClosing(IConnectionContext context, JabberId sessionJid);
	void sessionClosed(IConnectionContext context, JabberId sessionJid);
}
