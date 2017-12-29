package com.cloudeggtech.granite.framework.core.connection;

import com.cloudeggtech.basalt.protocol.core.JabberId;

public interface IConnectionManager {
	IConnectionContext getConnectionContext(JabberId sessionJid);
}
