package com.cloudeggtech.granite.framework.core.session;

import com.cloudeggtech.basalt.protocol.core.JabberId;

public interface ISessionManager {
	ISession create(JabberId jid) throws SessionExistsException;
	void put(JabberId jid, ISession session);
	ISession get(JabberId jid);
	boolean exists(JabberId jid);
	boolean remove(JabberId jid);
}
