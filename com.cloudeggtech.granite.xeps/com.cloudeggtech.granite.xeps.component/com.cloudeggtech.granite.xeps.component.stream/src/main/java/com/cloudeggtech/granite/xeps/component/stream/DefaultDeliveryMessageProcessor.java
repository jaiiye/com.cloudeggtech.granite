package com.cloudeggtech.granite.xeps.component.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;
import com.cloudeggtech.granite.framework.core.integration.IMessage;
import com.cloudeggtech.granite.framework.core.integration.IMessageProcessor;

@Component("default.component.delivery.message.processor")
public class DefaultDeliveryMessageProcessor implements IMessageProcessor {
	private Logger logger = LoggerFactory.getLogger(DefaultDeliveryMessageProcessor.class);

	@Override
	public void process(IConnectionContext context, IMessage message) {
		try {
			context.write(message.getPayload());
		} catch (Exception e) {
			logger.error("routing error", e);
		}
		
	}

}
