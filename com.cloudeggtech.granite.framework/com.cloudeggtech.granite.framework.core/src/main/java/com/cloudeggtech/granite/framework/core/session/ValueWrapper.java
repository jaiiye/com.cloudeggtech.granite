package com.cloudeggtech.granite.framework.core.session;

public interface ValueWrapper<T> {
	void setValue(T value);
	T getValue();
}
