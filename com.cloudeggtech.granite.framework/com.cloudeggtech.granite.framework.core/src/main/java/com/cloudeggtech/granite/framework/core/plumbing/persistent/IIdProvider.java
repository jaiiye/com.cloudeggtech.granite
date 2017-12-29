package com.cloudeggtech.granite.framework.core.plumbing.persistent;

public interface IIdProvider<T> {
	void setId(T id);
	T getId();
}
