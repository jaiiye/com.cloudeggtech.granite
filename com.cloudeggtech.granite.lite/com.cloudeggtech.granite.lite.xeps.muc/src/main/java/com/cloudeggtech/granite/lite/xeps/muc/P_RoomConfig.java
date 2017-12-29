package com.cloudeggtech.granite.lite.xeps.muc;

import com.cloudeggtech.granite.framework.core.plumbing.persistent.IIdProvider;

public class P_RoomConfig extends com.cloudeggtech.basalt.xeps.muc.RoomConfig implements IIdProvider<String> {
	private String id;
	private String roomId;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
}
