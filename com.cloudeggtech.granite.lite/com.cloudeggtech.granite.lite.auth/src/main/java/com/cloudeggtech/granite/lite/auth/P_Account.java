package com.cloudeggtech.granite.lite.auth;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;

public class P_Account extends com.cloudeggtech.granite.framework.core.auth.Account implements IIdProvider<String> {
	private String id;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}
}
