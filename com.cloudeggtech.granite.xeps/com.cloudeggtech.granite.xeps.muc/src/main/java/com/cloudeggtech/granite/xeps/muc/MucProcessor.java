package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.im.stanza.Presence;
import com.cloudeggtech.basalt.xeps.muc.Muc;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class MucProcessor implements IXepProcessor<Presence, Muc> {
	@Dependency("muc.protocols.processor")
	private MucProtocolsProcessor delegate;

	@Override
	public void process(IProcessingContext context, Presence presence, Muc muc) {
		delegate.process(context, presence, muc);
	}

}
