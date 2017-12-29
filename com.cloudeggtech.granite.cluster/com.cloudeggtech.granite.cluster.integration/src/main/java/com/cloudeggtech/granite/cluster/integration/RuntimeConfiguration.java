package com.cloudeggtech.granite.cluster.integration;

import com.cloudeggtech.granite.cluster.node.commons.deploying.DeployPlan;

public class RuntimeConfiguration {
	private String nodeType;
	private DeployPlan deployConfiguration;
	
	public RuntimeConfiguration(String nodeType, DeployPlan deployConfiguration) {
		this.nodeType = nodeType;
		this.deployConfiguration = deployConfiguration;
	}

	public String getNodeType() {
		return nodeType;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	public DeployPlan getDeployConfiguration() {
		return deployConfiguration;
	}
	
	public void setDeployConfiguration(DeployPlan deployConfiguration) {
		this.deployConfiguration = deployConfiguration;
	}
	
}
