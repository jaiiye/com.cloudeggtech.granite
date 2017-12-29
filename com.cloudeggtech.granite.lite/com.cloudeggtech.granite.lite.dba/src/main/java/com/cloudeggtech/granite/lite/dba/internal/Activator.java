package com.cloudeggtech.granite.lite.dba.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IPersistentObjectFactory;

public class Activator implements BundleActivator {
	private ServiceRegistration<IPersistentObjectFactory> srPersistentObjectFactory;
	@Override
	public void start(BundleContext context) throws Exception {
		srPersistentObjectFactory = context.registerService(IPersistentObjectFactory.class,
				new PersistentObjectFactory(context), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		srPersistentObjectFactory.unregister();
	}

}
