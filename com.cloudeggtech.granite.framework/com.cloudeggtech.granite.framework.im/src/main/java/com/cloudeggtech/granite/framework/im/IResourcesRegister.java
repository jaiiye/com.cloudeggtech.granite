package com.cloudeggtech.granite.framework.im;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;

public interface IResourcesRegister {
	boolean register(JabberId jid);
	boolean unregister(JabberId jid);
	
	boolean setRosterRequested(JabberId jid);
	boolean setBroadcastPresence(JabberId jid, Presence presence);
	boolean setAvailable(JabberId jid);
	boolean setDirectedPresence(JabberId from, JabberId to, Presence presence);
}
