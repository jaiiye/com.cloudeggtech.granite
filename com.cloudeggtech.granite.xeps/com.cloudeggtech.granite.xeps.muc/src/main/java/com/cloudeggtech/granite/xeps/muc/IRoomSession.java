package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Message;

public interface IRoomSession {
	Room getRoom();
	Occupant[] getOccupants();
	Occupant getOccupant(String nick);
	void enter(JabberId sessionJid, String nick);
	void enter(JabberId sessionJid, String nick, String secret);
	void exit(JabberId sessionJid);
	void setSubject(Message message);
	Message getSubject();
	void addToDiscussionHistory(String nick, JabberId oFrom, Message message);
	Message[] getDiscussionHistory();
	void changeNick(JabberId sessionJid, String nick);
}
