package com.cloudeggtech.granite.im;

import com.cloudeggtech.basalt.protocol.core.stanza.Stanza;
import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.granite.framework.core.event.IEventService;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;

public interface IChatMessageDeliverer {
	boolean isMessageDeliverable(IProcessingContext context, Stanza stanza);
	void deliver(IProcessingContext context, IEventService eventService, Message message);
}