package com.cloudeggtech.granite.framework.im;

import com.cloudeggtech.basalt.protocol.core.JabberId;

public interface IResourcesService {
	IResource[] getResources(JabberId jid);
	IResource getResource(JabberId jid);
}
