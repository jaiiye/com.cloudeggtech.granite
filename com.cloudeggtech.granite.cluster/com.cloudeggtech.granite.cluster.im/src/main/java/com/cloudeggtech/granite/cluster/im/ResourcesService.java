package com.cloudeggtech.granite.cluster.im;

import org.springframework.stereotype.Component;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;
import com.cloudeggtech.granite.framework.im.IResource;
import com.cloudeggtech.granite.framework.im.IResourcesRegister;
import com.cloudeggtech.granite.framework.im.IResourcesService;

@Component
public class ResourcesService implements IResourcesService, IResourcesRegister {

	@Override
	public boolean register(JabberId jid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unregister(JabberId jid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setRosterRequested(JabberId jid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setBroadcastPresence(JabberId jid, Presence presence) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setAvailable(JabberId jid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setDirectedPresence(JabberId from, JabberId to, Presence presence) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IResource[] getResources(JabberId jid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource(JabberId jid) {
		// TODO Auto-generated method stub
		return null;
	}

}
