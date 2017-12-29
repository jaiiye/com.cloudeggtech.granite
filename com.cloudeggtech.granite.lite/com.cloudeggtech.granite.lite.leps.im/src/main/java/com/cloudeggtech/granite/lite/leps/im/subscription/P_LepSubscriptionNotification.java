package com.cloudeggtech.granite.lite.leps.im.subscription;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;
import com.cloudeggtech.granite.leps.im.subscription.LepSubscriptionNotification;

public class P_LepSubscriptionNotification extends LepSubscriptionNotification
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
