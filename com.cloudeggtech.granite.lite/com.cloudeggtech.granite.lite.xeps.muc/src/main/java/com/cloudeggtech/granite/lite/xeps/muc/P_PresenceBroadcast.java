package com.cloudeggtech.granite.lite.xeps.muc;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;

public class P_PresenceBroadcast extends com.cloudeggtech.basalt.xeps.muc.PresenceBroadcast implements IIdProvider<String> {
	private String id;
	private String roomConfigId;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getRoomConfigId() {
		return roomConfigId;
	}

	public void setRoomConfigId(String roomConfigId) {
		this.roomConfigId = roomConfigId;
	}
	
}
