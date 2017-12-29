package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.xeps.muc.owner.MucOwner;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class MucOwnerProcessor implements IXepProcessor<Iq, MucOwner> {
	@Dependency("muc.protocols.processor")
	private MucProtocolsProcessor delegate;

	@Override
	public void process(IProcessingContext context, Iq iq, MucOwner mucOwner) {
		delegate.process(context, iq, mucOwner);
	}

}
