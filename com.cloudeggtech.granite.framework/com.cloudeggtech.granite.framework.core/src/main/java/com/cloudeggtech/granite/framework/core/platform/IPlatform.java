package com.cloudeggtech.granite.framework.core.platform;

import java.net.URL;

public interface IPlatform {
	URL getHomeDirectory();
	URL getConfigurationDirectory();
}
