package com.cloudeggtech.granite.cluster.node.mgtnode.deploying.pack;

import com.cloudeggtech.granite.cluster.node.commons.deploying.DeployPlan;

public interface IPackConfigurator {
	void configure(IPackContext context, DeployPlan configuration);
}
