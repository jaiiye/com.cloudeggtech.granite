package com.cloudeggtech.granite.framework.core.repository;

import com.cloudeggtech.granite.framework.core.IService;

public interface IServiceWrapper {
	String getId();
	IService create() throws ServiceCreationException;
}
