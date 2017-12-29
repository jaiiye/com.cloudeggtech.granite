package com.cloudeggtech.granite.cluster.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.stanza.Stanza;
import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.integration.IMessage;
import com.cloudeggtech.granite.framework.core.integration.IMessageChannel;
import com.cloudeggtech.granite.framework.core.routing.IForward;
import com.cloudeggtech.granite.framework.core.routing.IRouter;

@Component("cluster.routing.2.stream.message.channel")
public class Routing2StreamMessageChannel implements IMessageChannel {
	private static final Logger logger = LoggerFactory.getLogger(Routing2StreamMessageChannel.class);
	
	@Dependency("runtime.configuration")
	private RuntimeConfiguration runtimeConfiguration;
	
	@Dependency("router")
	private IRouter router;

	@Override
	public void send(IMessage message) {
		JabberId target = (JabberId)message.getHeader().get(IMessage.KEY_MESSAGE_TARGET);
		if (target != null) {
			Object payload = message.getPayload();
			if (payload instanceof Stanza) {
				target = ((Stanza)payload).getTo();
			}
		}
		
		if (target == null) {
			logger.warn("Null message target. Message content: {}.", message.getPayload());
			return;
		}
		
		IForward[] forwards = router.get(target);
		if (forwards == null || forwards.length == 0) {
			logger.warn("Can't forward message. Message content: {}. Message Target: {}", message.getPayload(), target.toString());
			// TODO Process offline messages.
			return;
		}
		
		for (IForward forward : forwards) {
			forward.to(message);
		}
	}

}
