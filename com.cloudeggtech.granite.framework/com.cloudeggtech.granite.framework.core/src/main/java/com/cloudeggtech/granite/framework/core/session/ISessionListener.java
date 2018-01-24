package com.cloudeggtech.granite.framework.core.session;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;

public interface ISessionListener {
	void sessionEstablishing(IConnectionContext context, JabberId sessionJid) throws Exception;
	void sessionEstablished(IConnectionContext context, JabberId sessionJid) throws Exception;
	void sessionClosing(IConnectionContext context, JabberId sessionJid) throws Exception;
	void sessionClosed(IConnectionContext context, JabberId sessionJid) throws Exception;
}
