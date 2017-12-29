package com.cloudeggtech.granite.framework.core.plumbing;

import org.osgi.framework.BundleContext;

public interface IApplicationComponentService {
	void inject(Object object, BundleContext bundleContext);
}
