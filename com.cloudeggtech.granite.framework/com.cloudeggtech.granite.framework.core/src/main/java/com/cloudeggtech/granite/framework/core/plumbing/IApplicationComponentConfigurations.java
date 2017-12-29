package com.cloudeggtech.granite.framework.core.plumbing;

import com.cloudeggtech.granite.framework.core.config.IConfiguration;

public interface IApplicationComponentConfigurations {
	IConfiguration getConfiguration(String bundleSymbolicName);
}