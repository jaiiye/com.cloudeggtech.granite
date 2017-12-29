package com.cloudeggtech.granite.cluster.node.mgtnode.deploying.pack;

import java.nio.file.Path;

import com.cloudeggtech.granite.cluster.node.commons.deploying.DeployPlan;
import com.cloudeggtech.granite.cluster.node.mgtnode.deploying.pack.config.IConfigManager;

public interface IPackContext {
	Path getConfigDir();
	Path getRepositoryDir();
	Path getRuntimeDir();
	Path getRuntimePluginsDir();
	Path getRuntimeOsgiConfigDir();
	Path getRuntimeGraniteConfigDir();
	String getNodeType();
	DeployPlan getDeployConfiguration();
	IPackModule getPackModule(String moduleName);
	IConfigManager getConfigManager();
}
