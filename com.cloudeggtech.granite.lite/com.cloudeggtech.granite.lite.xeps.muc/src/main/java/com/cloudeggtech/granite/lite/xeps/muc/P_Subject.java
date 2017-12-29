package com.cloudeggtech.granite.lite.xeps.muc;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;
import com.cloudeggtech.granite.xeps.muc.Subject;

public class P_Subject extends Subject implements IIdProvider<String> {
	private String id;
	private String roomId;

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
}
