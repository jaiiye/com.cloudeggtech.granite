package com.cloudeggtech.granite.framework.parsing.internal;

import com.cloudeggtech.granite.framework.core.IService;
import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.integration.IMessageReceiver;

@Component("parsing.service")
public class ParsingService implements IService {
	
	@Dependency("stream.message.receiver")
	private IMessageReceiver parsingMessageReceiver;

	@Override
	public void start() throws Exception {
		parsingMessageReceiver.start();
	}

	@Override
	public void stop() throws Exception {
		parsingMessageReceiver.stop();
	}

}
