package com.cloudeggtech.granite.cluster.integration;

import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.integration.IMessage;
import com.cloudeggtech.granite.framework.core.integration.IMessageChannel;

@Component("cluster.any.2.event.message.channel")
public class Any2EventMessageChannel implements IMessageChannel {

	@Override
	public void send(IMessage message) {
		// TODO Auto-generated method stub

	}

}
