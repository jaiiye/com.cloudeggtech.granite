package com.cloudeggtech.granite.framework.stream;

import com.cloudeggtech.granite.framework.core.connection.IClientConnectionContext;
import com.cloudeggtech.granite.framework.core.integration.IMessage;

public interface IStreamNegotiant {
	void setNext(IStreamNegotiant next);
	boolean negotiate(IClientConnectionContext context, IMessage message);
}
