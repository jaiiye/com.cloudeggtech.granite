package com.cloudeggtech.granite.cluster.integration.ignite.config;

public class CacheStorage extends Storage {
	public enum EvictionPolicy {
		RANDOM_LRU,
		RANDOM_2_LRU
	}
	
	public static final String NAME_CACHE_STORAGE = "cache-storage";
	
	private EvictionPolicy evicationPolicy;

	public CacheStorage() {
		initSize = 64 * 1024 * 1024;
		maxSize = 256 * 1024 * 1024;
		persistenceEnabled = false;
		evicationPolicy = EvictionPolicy.RANDOM_2_LRU;
	}
	
	public EvictionPolicy getEvicationPolicy() {
		return evicationPolicy;
	}

	public void setEvicationPolicy(EvictionPolicy evicationPolicy) {
		this.evicationPolicy = evicationPolicy;
	}
	
}
