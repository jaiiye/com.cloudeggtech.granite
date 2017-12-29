package com.cloudeggtech.granite.xeps.disco;

import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.xeps.disco.DiscoItems;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class DiscoItemsProcessor implements IXepProcessor<Iq, DiscoItems> {
	@Dependency("disco.processor")
	private IDiscoProcessor discoProcessor;

	@Override
	public void process(IProcessingContext context, Iq iq, DiscoItems discoItems) {
		discoProcessor.discoItems(context, iq, iq.getTo() == null ? context.getJid() : iq.getTo(), discoItems.getNode());
	}

}
