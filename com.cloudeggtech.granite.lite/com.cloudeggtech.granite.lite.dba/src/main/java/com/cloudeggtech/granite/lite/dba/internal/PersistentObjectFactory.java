package com.cloudeggtech.granite.lite.dba.internal;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.cloudeggtech.granite.framework.core.commons.osgi.IContributionTracker;
import com.cloudeggtech.granite.framework.core.commons.osgi.OsgiUtils;
import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;
import com.cloudeggtech.granite.framework.core.plumbing.persistent.IPersistentObjectFactory;

public class PersistentObjectFactory implements IPersistentObjectFactory {
	private static final String KEY_GRANITE_PERSISTENT_OBJECTS = "Granite-MyBatis-Persistent-Objects";
	
	private Map<Class<?>, PersistentObjectMapping> objectMappings;
	private IContributionTracker tracker;
	
	public PersistentObjectFactory(BundleContext bundleContext) {
		objectMappings = new ConcurrentHashMap<>();
		tracker = new PersistentObjectsTracker();
		
		OsgiUtils.trackContribution(bundleContext, KEY_GRANITE_PERSISTENT_OBJECTS, tracker);
	}
	
	private class PersistentObjectMapping {
		public Class<?> domain;
		public Class<?> persistent;
		public String bundleSymlicName;
		
		public PersistentObjectMapping(Class<?> domain, Class<?> persistent, String bundleSymlicName) {
			this.domain = domain;
			this.persistent = persistent;
			this.bundleSymlicName = bundleSymlicName;
		}
	}
	
	private class PersistentObjectsTracker implements IContributionTracker {

		@Override
		public void found(Bundle bundle, String contribution) throws Exception {
			String symbolicName = bundle.getSymbolicName();
			StringTokenizer st = new StringTokenizer(contribution, ",");
			while (st.hasMoreTokens()) {
				String sPersistentConfig = st.nextToken();
				int equalMarkIndex = sPersistentConfig.indexOf('=');
				String sDomain = null;
				String sPersistent = null;
				if (equalMarkIndex == -1) {
					sPersistent = sPersistentConfig;
				} else {
					sDomain = sPersistentConfig.substring(0, equalMarkIndex);
					sPersistent = sPersistentConfig.substring(equalMarkIndex + 1);
				}
				
				Class<?> persistent = bundle.loadClass(sPersistent);
				
				Class<?> domain = null;
				if (sDomain != null) {
					domain = bundle.loadClass(sDomain);
				} else {
					domain = persistent.getSuperclass();
					
					if (domain.equals(Object.class)) {
						continue;
					}
				}
				
				PersistentObjectMapping mapping = new PersistentObjectMapping(domain, persistent, symbolicName);
				objectMappings.put(mapping.domain, mapping);
			}
		}

		@Override
		public void lost(Bundle bundle, String contribution) throws Exception {
			String symbolicName = bundle.getSymbolicName();
			for (PersistentObjectMapping objectMapping : objectMappings.values()) {
				if (objectMapping.bundleSymlicName.equals(symbolicName))
					objectMappings.remove(objectMapping.domain);
			}
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <K, V extends K> V create(Class<K> clazz) {
		try {
			V object = doCreate(clazz);
			
			if (object instanceof IIdProvider) {
				((IIdProvider)object).setId(UUID.randomUUID().toString());
			}
			
			return object;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Can't create persistent object for class %s.", clazz.getName()), e);
		}
	}

	@SuppressWarnings("unchecked")
	private <K, V extends K> V doCreate(Class<K> clazz) throws InstantiationException, IllegalAccessException {
		PersistentObjectMapping objectMapping = objectMappings.get(clazz);
		if (objectMapping == null) {
			return (V)clazz.newInstance();
		}
		
		return (V)objectMapping.persistent.newInstance();
	}

}
