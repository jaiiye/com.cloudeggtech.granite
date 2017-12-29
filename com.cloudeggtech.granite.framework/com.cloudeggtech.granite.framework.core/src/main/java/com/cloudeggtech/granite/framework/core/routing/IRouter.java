package com.cloudeggtech.granite.framework.core.routing;

import com.cloudeggtech.basalt.protocol.core.JabberId;

public interface IRouter {
	void register(JabberId jid, String localNodeId) throws RoutingRegistrationException;
	void unregister(JabberId jid) throws RoutingRegistrationException;
	
	IForward[] get(JabberId jid);
}
