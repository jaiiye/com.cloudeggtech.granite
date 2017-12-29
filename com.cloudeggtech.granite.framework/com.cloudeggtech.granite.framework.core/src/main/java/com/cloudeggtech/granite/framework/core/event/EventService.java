package com.cloudeggtech.granite.framework.core.event;

import com.cloudeggtech.granite.framework.core.integration.IMessageChannel;
import com.cloudeggtech.granite.framework.core.integration.SimpleMessage;

public class EventService implements IEventService {
	private IMessageChannel eventMessageChannel;
	
	public EventService(IMessageChannel eventMessageChannel) {
		this.eventMessageChannel = eventMessageChannel;
	}

	@Override
	public void fire(IEvent event) {
		eventMessageChannel.send(new SimpleMessage(event));
	}

}
