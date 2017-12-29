package com.cloudeggtech.granite.framework.processing;

import com.cloudeggtech.basalt.protocol.im.stanza.Message;

public interface IMessageListener {
	void received(Message message);
}
