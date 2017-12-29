package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.basalt.xeps.muc.xconference.XConference;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class XConferenceProcessor implements IXepProcessor<Message, XConference> {
	@Dependency("muc.protocols.processor")
	private MucProtocolsProcessor delegate;

	@Override
	public void process(IProcessingContext context, Message message, XConference xConference) {
		delegate.process(context, message, xConference);
	}

}
