package com.cloudeggtech.granite.framework.core.plumbing.persistent;

public interface IPersistentObjectFactory {
	<K, V extends K> V create(Class<K> clazz);
}
