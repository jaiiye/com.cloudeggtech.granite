package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.xeps.muc.admin.MucAdmin;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class MucAdminProcessor implements IXepProcessor<Iq, MucAdmin> {
	@Dependency("muc.protocols.processor")
	private MucProtocolsProcessor delegate;

	@Override
	public void process(IProcessingContext context, Iq iq, MucAdmin mucAdmin) {
		delegate.process(context, iq, mucAdmin);
	}

}
