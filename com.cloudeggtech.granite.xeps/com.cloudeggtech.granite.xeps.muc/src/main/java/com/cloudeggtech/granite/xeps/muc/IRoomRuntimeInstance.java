package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.basalt.xeps.muc.Role;

public interface IRoomRuntimeInstance {
	void setSubject(Message subject);
	Message getSubject();
	Occupant[] getOccupants();
	Occupant getOccupant(String nick);
	void enter(JabberId sessionJid, String nick, Role role);
	void exit(JabberId sessionJid);
	void addToDiscussionHistory(Message message);
	Message[] getDiscussionHistory();
	void changeNick(JabberId sessionJid, String nick);
}
