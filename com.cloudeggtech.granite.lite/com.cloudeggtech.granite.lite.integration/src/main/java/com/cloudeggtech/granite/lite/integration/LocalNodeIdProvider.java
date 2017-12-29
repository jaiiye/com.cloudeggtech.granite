package com.cloudeggtech.granite.lite.integration;

import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.routing.ILocalNodeIdProvider;

@Component("lite.local.node.id.provider")
public class LocalNodeIdProvider implements ILocalNodeIdProvider {

	@Override
	public String getLocalNodeId() {
		return null;
	}

}
