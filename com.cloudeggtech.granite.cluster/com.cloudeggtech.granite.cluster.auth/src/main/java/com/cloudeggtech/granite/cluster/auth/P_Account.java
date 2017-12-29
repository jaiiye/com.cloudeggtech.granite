package com.cloudeggtech.granite.cluster.auth;

import com.cloudeggtech.granite.framework.core.auth.Account;
import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;

public class P_Account extends Account implements IIdProvider<String> {
	private String id;

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

}
