package com.cloudeggtech.granite.framework.core.connection;

import com.cloudeggtech.granite.framework.core.session.ISession;

public interface IConnectionContext extends ISession {
	void write(Object message);
	void close();
}
