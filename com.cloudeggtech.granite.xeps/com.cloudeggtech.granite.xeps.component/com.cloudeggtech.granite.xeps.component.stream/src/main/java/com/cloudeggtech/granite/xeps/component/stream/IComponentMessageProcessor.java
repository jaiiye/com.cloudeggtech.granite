package com.cloudeggtech.granite.xeps.component.stream;

import com.cloudeggtech.granite.framework.core.integration.IMessageProcessor;

public interface IComponentMessageProcessor extends IMessageProcessor {
	void setComponentConnectionsRegister(IComponentConnectionsRegister connectionsRegister);
}
