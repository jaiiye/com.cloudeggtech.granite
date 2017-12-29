package com.cloudeggtech.granite.lite.leps.im.traceable;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;
import com.cloudeggtech.granite.leps.im.traceable.TraceableMessage;

public class P_TraceableMessage extends TraceableMessage implements IIdProvider<String> {
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
