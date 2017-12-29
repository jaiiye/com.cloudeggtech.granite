package com.cloudeggtech.granite.lite.xeps.msgoffline;

import java.util.List;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.im.OfflineMessage;

public interface OfflineMessageMapper {
	void insert(OfflineMessage offlineMessage);
	List<OfflineMessage> selectByJid(JabberId jid, int limit, int offset);
	void deleteByJidAndMessageId(JabberId jid, String messageId);
	int selectCountByJid(JabberId jid);
}
