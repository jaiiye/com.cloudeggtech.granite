package com.cloudeggtech.granite.leps.im.subscription;

import com.cloudeggtech.basalt.leps.im.subscription.Subscribe;
import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class SubscribeProcessor implements IXepProcessor<Iq, Subscribe> {
	@Dependency("subscription.protocols.processor")
	private SubscriptionProtocolsProcessor delegate;

	@Override
	public void process(IProcessingContext context, Iq iq, Subscribe subscribe) {
		delegate.processSubscribe(context, iq, subscribe);
	}

}
