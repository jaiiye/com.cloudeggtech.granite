package com.cloudeggtech.granite.framework.im;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;

public interface IResourcesRegister {
	void register(JabberId jid) throws ResourceRegistrationException;
	void unregister(JabberId jid) throws ResourceRegistrationException;
	
	void setRosterRequested(JabberId jid) throws ResourceRegistrationException;
	void setBroadcastPresence(JabberId jid, Presence presence) throws ResourceRegistrationException;
	void setAvailable(JabberId jid) throws ResourceRegistrationException;
	void setDirectedPresence(JabberId from, JabberId to, Presence presence) throws ResourceRegistrationException;
}
