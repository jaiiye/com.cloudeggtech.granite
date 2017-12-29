package com.cloudeggtech.granite.framework.event.internal;

import com.cloudeggtech.granite.framework.core.IService;
import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.integration.IMessageReceiver;

@Component("event.service")
public class EventService implements IService {
	@Dependency("event.message.receiver")
	private IMessageReceiver eventMessageReceiver;
	
	@Override
	public void start() throws Exception {
		eventMessageReceiver.start();
	}

	@Override
	public void stop() throws Exception {
		eventMessageReceiver.stop();
	}

}
