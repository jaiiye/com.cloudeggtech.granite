package com.cloudeggtech.granite.framework.im;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;

public interface IResource {
	JabberId getJid();
	boolean isRosterRequested();
	Presence getBroadcastPresence();
	boolean isAvailable();
	Presence getDirectedPresence(JabberId from);
}
