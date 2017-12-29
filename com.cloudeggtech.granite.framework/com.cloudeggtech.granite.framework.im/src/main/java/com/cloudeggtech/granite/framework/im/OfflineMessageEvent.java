package com.cloudeggtech.granite.framework.im;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.granite.framework.core.event.IEvent;

public class OfflineMessageEvent implements IEvent {
	private JabberId user;
	private JabberId contact;
	private Message message;
	
	public OfflineMessageEvent(JabberId user, JabberId contact, Message message) {
		this.user = user;
		this.contact = contact;
		this.message = message;
	}

	public JabberId getUser() {
		return user;
	}

	public void setUser(JabberId user) {
		this.user = user;
	}

	public JabberId getContact() {
		return contact;
	}

	public void setContact(JabberId contact) {
		this.contact = contact;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
}
