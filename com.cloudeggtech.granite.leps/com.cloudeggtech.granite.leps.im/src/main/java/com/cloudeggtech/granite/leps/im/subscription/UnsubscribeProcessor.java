package com.cloudeggtech.granite.leps.im.subscription;

import com.cloudeggtech.basalt.leps.im.subscription.Unsubscribe;
import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class UnsubscribeProcessor implements IXepProcessor<Iq, Unsubscribe> {
	@Dependency("subscription.protocols.processor")
	private SubscriptionProtocolsProcessor delegate;

	@Override
	public void process(IProcessingContext context, Iq iq, Unsubscribe unsubscribe) {
		delegate.processUnsubscribe(context, iq, unsubscribe);
	}

}
