package com.cloudeggtech.granite.framework.core.repository;

public interface IServiceListener {
	void available(IServiceWrapper serviceWrapper);
	void unavailable(String serviceId);
}
