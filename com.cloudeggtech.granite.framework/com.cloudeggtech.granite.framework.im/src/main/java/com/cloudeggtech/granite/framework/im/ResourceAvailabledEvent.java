package com.cloudeggtech.granite.framework.im;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.core.event.IEvent;

public class ResourceAvailabledEvent implements IEvent {
	private JabberId jid;
	
	public ResourceAvailabledEvent(JabberId jid) {
		this.jid = jid;
	}
	
	public JabberId getJid() {
		return jid;
	}
}
