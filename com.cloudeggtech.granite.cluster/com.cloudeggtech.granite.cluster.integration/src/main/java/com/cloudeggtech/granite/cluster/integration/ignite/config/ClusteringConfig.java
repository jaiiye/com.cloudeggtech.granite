package com.cloudeggtech.granite.cluster.integration.ignite.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.cloudeggtech.granite.cluster.integration.ignite.config.Discovery.Strategy;
import com.cloudeggtech.granite.framework.core.commons.utils.SectionalProperties;
import com.cloudeggtech.granite.framework.core.commons.utils.StringUtils;

public class ClusteringConfig {
	private Discovery discovery;
	private StorageGlobal storageGlobal;
	private SessionStorage sessionStorage;
	private CacheStorage cacheStorage;
	
	public void load(File configFile) {
		SectionalProperties properties = new SectionalProperties();
		try {
			properties.load(new FileInputStream(configFile));
		} catch (Exception e) {
			throw new RuntimeException("Can't load clustering.ini.", e);
		}
		
		for (String sectionName : properties.getSectionNames()) {
			if (Discovery.NAME_DISCOVERY.equals(sectionName)) {
				discovery = parseDiscovery(properties.getSection(Discovery.NAME_DISCOVERY));
			} else if (StorageGlobal.NAME_STORAGE_GLOBAL.equals(sectionName)) {
				storageGlobal = parseStorageGlobal(properties.getSection(StorageGlobal.NAME_STORAGE_GLOBAL));
			} else if (SessionStorage.NAME_SESSION_STORAGE.equals(sectionName)) {
				sessionStorage = parseSessionStorage(properties.getSection(SessionStorage.NAME_SESSION_STORAGE));
			} else if (CacheStorage.NAME_CACHE_STORAGE.equals(sectionName)) {
				cacheStorage = parseCacheStorage(properties.getSection(CacheStorage.NAME_CACHE_STORAGE));
			} else {
				throw new IllegalArgumentException(String.format("Invalid section in clustering.ini. Section name: %s", sectionName));
			}
		}
	}
	
	private CacheStorage parseCacheStorage(Properties properties) {
		// TODO Auto-generated method stub
		return null;
	}

	private SessionStorage parseSessionStorage(Properties properties) {
		sessionStorage = new SessionStorage();
		parseStorage(sessionStorage, properties, SessionStorage.NAME_SESSION_STORAGE);
		
		String sSessionDurationTime = properties.getProperty("session-duration-time");
		try {
			sessionStorage.setSessionDurationTime(Integer.parseInt(sSessionDurationTime));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Parameter 'session-duration-time' in 'session-storage' section must be an integer.");
		}
		
		return sessionStorage;
	}

	private void parseStorage(Storage storage, Properties properties, String storageSectionName) {
		String initSize = properties.getProperty("init-size");
		if (initSize != null) {
			try {
				setStorageInitSize(sessionStorage, convertToByteSizes(initSize));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Parameter 'init-size' of '%s' configuration section must be an long integer.",
						storageSectionName));
			}
		}
		
		String maxSize = properties.getProperty("max-size");
		if (maxSize != null) {
			try {
				setStorageMaxSize(sessionStorage, convertToByteSizes(maxSize));
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("Parameter 'max-size' of '%s' configuration section must be an long integer.",
						storageSectionName));
			}
		}
		
		String persistenceEnabled = properties.getProperty("persistence-enabled");
		if (persistenceEnabled != null) {
			setStoragePersisenceEnabled(storage, persistenceEnabled);
		}
	}

	private void setStoragePersisenceEnabled(Storage storage, String persistenceEnabled) {
		storage.setPersistenceEnabled(Boolean.parseBoolean(persistenceEnabled));
	}

	private void setStorageMaxSize(Storage storage, long maxSize) {
		storage.setMaxSize(maxSize);
	}

	private void setStorageInitSize(Storage storage, long initSize) {
		storage.setInitSize(initSize);
	}

	private StorageGlobal parseStorageGlobal(Properties properties) {
		StorageGlobal storageGlobal = new StorageGlobal();
		String workDirectory = properties.getProperty("work-directory");
		if (workDirectory != null) {
			storageGlobal.setWorkDirectory(workDirectory);
		}
		
		String pageSize = properties.getProperty("page-size");
		if (pageSize != null) {
			try {
				storageGlobal.setPageSize((int)convertToByteSizes(pageSize));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Parameter 'page-size' of 'storage-global' configuration section must be an integer.", e);
			}
		}
		
		String storagePath = properties.getProperty("storage-path");
		if (storagePath != null) {
			if (!new File(storagePath).exists())
				throw new IllegalArgumentException(String.format("Invalid storage path. Path %s doesn't exist.", storagePath));
			
			storageGlobal.setStoragePath(storagePath);
		}
		
		String walPath = properties.getProperty("wal-path");
		if (walPath != null) {
			if (!new File(walPath).exists())
				throw new IllegalArgumentException(String.format("Invalid WAL path. Path %s doesn't exist.", walPath));
			
			storageGlobal.setWalPath(walPath);
		}
		
		return storageGlobal;
	}

	private long convertToByteSizes(String size) {
		char lastChar = size.charAt(size.length() - 1);
		try {
			Long.parseLong(Character.toString(lastChar));
			
			return Long.parseLong(size);
		} catch (Exception e) {
			// ignore
		}
		
		String sizeNumberPart = size.substring(0, size.length() - 1);
		if (lastChar == 'b' || lastChar == 'B') {
			return Long.parseLong(sizeNumberPart);
		} else if (lastChar == 'k' || lastChar == 'K') {
			return Long.parseLong(sizeNumberPart) * 1024;
		} else if (lastChar == 'm' || lastChar == 'M') {
			return Long.parseLong(sizeNumberPart) * 1024 * 1024;
		} else if (lastChar == 'g' || lastChar == 'G') {
			return Long.parseLong(sizeNumberPart) * 1024 * 1024 * 1024;
		}  else {
			throw new IllegalArgumentException(String.format("Unknown unit of size: %c.", lastChar));
		}
	}

	private Discovery parseDiscovery(Properties properties) {
		discovery = new Discovery();
		
		String sStrategy = properties.getProperty("strategy");
		if (sStrategy != null) {
			if ("multicast".equals(sStrategy)) {
				discovery.setStrategy(Strategy.MULTICAST);
			} else if ("static-ip".equals(sStrategy)) {
				discovery.setStrategy(Strategy.STATIC_IP);
			} else if ("multicast-and-static-ip".equals(sStrategy)) {
				discovery.setStrategy(Strategy.MULTICAST_AND_STATIC_IP);
			} else {
				throw new RuntimeException(String.format("Unsupported discovery strategy: %s. Supported discovery strategies are: %s.",
						sStrategy, "multicast, static-ip, multicat-and-static-ip"));
			}
		}
		
		String multicastGroup = properties.getProperty("multicast-group");
		if (discovery.getStrategy() == Strategy.MULTICAST && multicastGroup == null) {
			multicastGroup = "228.10.10.157";
		}
		discovery.setMulticastGroup(multicastGroup);
		
		String sUseMgtnodeStaticIp = properties.getProperty("use-mgtnode-static-ip", "false");
		discovery.setUseMgtnodeStaticIp(Boolean.parseBoolean(sUseMgtnodeStaticIp));
		
		String[] useStaticAddesses = StringUtils.stringToArray(properties.getProperty("use-static-addresses"));
		discovery.setStaticAddresses(useStaticAddesses);
		
		return discovery;
	}

	public Discovery getDiscovery() {
		if (discovery == null) {
			discovery = new Discovery();
		}
		
		return discovery;
	}
	
	public StorageGlobal getStorageGlobal() {
		if (storageGlobal == null)
			storageGlobal = new StorageGlobal();
		
		return storageGlobal;
	}
	
	public SessionStorage getSessionStorage() {
		if (sessionStorage == null)
			sessionStorage = new SessionStorage();
		
		return sessionStorage;
	}
	
	public CacheStorage getCacheStorage() {
		if (cacheStorage == null)
			cacheStorage = new CacheStorage();
		
		return cacheStorage;
	}
}
