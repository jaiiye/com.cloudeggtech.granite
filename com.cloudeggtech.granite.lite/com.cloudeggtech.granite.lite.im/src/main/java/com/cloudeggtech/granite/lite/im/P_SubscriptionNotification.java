package com.cloudeggtech.granite.lite.im;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;

public class P_SubscriptionNotification extends com.cloudeggtech.granite.framework.im.SubscriptionNotification
		implements IIdProvider<String> {
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
