package com.cloudeggtech.granite.framework.core.event;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.stanza.Stanza;

public interface IEventContext {
	void write(Stanza stanza);
	void write(JabberId target, Stanza stanza);
	void write(JabberId target, String message);
}
