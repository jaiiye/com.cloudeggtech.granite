package com.cloudeggtech.granite.xeps.disco;

import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.xeps.disco.DiscoInfo;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class DiscoInfoProcessor implements IXepProcessor<Iq, DiscoInfo> {
	@Dependency("disco.processor")
	private IDiscoProcessor discoProcessor;

	@Override
	public void process(IProcessingContext context, Iq iq, DiscoInfo discoInfo) {
		discoProcessor.discoInfo(context, iq, iq.getTo() == null ? context.getJid() : iq.getTo(), discoInfo.getNode()); 
	}

}
