package com.cloudeggtech.granite.framework.processing;

import com.cloudeggtech.basalt.protocol.im.stanza.Presence;

public interface IPresenceProcessor {
	boolean process(IProcessingContext context, Presence presence);
}
