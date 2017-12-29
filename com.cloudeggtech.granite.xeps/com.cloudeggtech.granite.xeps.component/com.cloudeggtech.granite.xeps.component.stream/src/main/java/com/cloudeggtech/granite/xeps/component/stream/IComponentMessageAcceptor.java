package com.cloudeggtech.granite.xeps.component.stream;

import com.cloudeggtech.granite.framework.core.integration.IMessageReceiver;

public interface IComponentMessageAcceptor extends IMessageReceiver {
	void setComponentConnectionsRegister(IComponentConnectionsRegister connectionsRegister);
}
