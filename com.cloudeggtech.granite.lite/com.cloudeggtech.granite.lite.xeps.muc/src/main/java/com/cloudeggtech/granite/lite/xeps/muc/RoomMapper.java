package com.cloudeggtech.granite.lite.xeps.muc;

import java.util.List;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.xeps.muc.GetMemberList;
import com.cloudeggtech.basalt.xeps.muc.PresenceBroadcast;
import com.cloudeggtech.basalt.xeps.muc.RoomConfig;
import com.cloudeggtech.granite.xeps.muc.AffiliatedUser;
import com.cloudeggtech.granite.xeps.muc.Room;
import com.cloudeggtech.granite.xeps.muc.RoomItem;

public interface RoomMapper {
	int selectCountByJid(String roomJid);
	int selectCount();
	List<RoomItem> selectRoomItems();
	Room selectByJid(String roomJid);
	List<AffiliatedUser> selectAffiliatedUsersByRoomId(String roomId);
	void updateLocked(String roomId, boolean locked);
	void insert(Room room);
	void insertRoomConfig(RoomConfig roomConfig);
	void insertRoomConfigPresenceBroadcast(PresenceBroadcast presenceBroadcast);
	void insertRoomConfigGetMemberList(GetMemberList getMemberList);
	void insertRoomAffiliatedUser(AffiliatedUser affiliatedUser);
	void updateRoomConfig(RoomConfig roomConfig);
	void updateRoomAffiliatedUser(AffiliatedUser affiliatedUser);
	void updateRoomConfigPresenceBroadcast(PresenceBroadcast presenceBroadcast);
	void updateRoomConfigGetMemberList(GetMemberList getMemberList);
	//void insertRoomSubjectHistory(Subject subject);
	JabberId selectAffiliatedUserJidByNick(JabberId roomJid, String nick);
}
