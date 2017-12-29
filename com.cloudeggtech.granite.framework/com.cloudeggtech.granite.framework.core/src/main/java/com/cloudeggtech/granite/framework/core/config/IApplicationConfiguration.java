package com.cloudeggtech.granite.framework.core.config;

public interface IApplicationConfiguration {
	String getConfigurationManagerBundleSymbolicName();	
	String getConfigurationManagerClass();
	String[] getDisabledServices();
	String getAppHome();
	String getConfigDir();
	String getDomainName();
	String[] getDomainAliasNames();
	String getComponentBindingProfile();
	String getLogConfigurationFile();
	String[] getApplicationNamespaces();
}
