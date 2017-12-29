package com.cloudeggtech.granite.lite.xeps.msgoffline;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;
import com.cloudeggtech.granite.framework.im.OfflineMessage;

public class P_OfflineMessage extends OfflineMessage implements IIdProvider<String> {
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
