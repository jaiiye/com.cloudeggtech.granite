package com.cloudeggtech.granite.framework.processing;

import com.cloudeggtech.basalt.protocol.im.stanza.Message;

public interface IMessageProcessor {
	boolean process(IProcessingContext context, Message message);
}
