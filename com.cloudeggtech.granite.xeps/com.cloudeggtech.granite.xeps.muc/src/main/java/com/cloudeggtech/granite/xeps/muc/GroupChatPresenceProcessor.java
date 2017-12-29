package com.cloudeggtech.granite.xeps.muc;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.processing.IPresenceProcessor;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;

public class GroupChatPresenceProcessor implements IPresenceProcessor {
	@Dependency("muc.protocols.processor")
	private MucProtocolsProcessor delegate;
	
	@Dependency("room.service")
	private IRoomService roomService;
	
	@Override
	public boolean process(IProcessingContext context, Presence presence) {
		if (presence.getType() != null && presence.getType() != Presence.Type.UNAVAILABLE)
			return false;
		
		if (presence.getTo() == null) {
			return false;
		}
		
		JabberId roomJid = presence.getTo().getBareId();
		if (!roomService.exists(roomJid)) {
			return false;
		}
		
		delegate.process(context, presence);
		
		return true;
	}

}

