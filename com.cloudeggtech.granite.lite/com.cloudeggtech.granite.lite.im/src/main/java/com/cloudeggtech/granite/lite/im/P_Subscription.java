package com.cloudeggtech.granite.lite.im;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;

public class P_Subscription extends com.cloudeggtech.granite.framework.im.Subscription implements IIdProvider<String> {
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
