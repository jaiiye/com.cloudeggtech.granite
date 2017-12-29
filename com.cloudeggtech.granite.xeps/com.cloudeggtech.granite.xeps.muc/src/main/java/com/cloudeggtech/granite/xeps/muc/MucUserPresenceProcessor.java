package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.im.stanza.Presence;
import com.cloudeggtech.basalt.xeps.muc.user.MucUser;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class MucUserPresenceProcessor implements IXepProcessor<Presence, MucUser> {
	@Dependency("muc.protocols.processor")
	private MucProtocolsProcessor delegate;

	@Override
	public void process(IProcessingContext context, Presence presence, MucUser mucUser) {
		delegate.process(context, presence, mucUser);
	}

}
