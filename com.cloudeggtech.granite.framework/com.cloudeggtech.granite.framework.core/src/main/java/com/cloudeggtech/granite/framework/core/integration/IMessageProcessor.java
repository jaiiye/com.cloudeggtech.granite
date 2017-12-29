package com.cloudeggtech.granite.framework.core.integration;

import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;

public interface IMessageProcessor {
	void process(IConnectionContext context, IMessage message);
}
