package com.cloudeggtech.granite.leps.im.roster;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.ProtocolException;
import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.protocol.core.stanza.error.BadRequest;
import com.cloudeggtech.basalt.protocol.im.roster.Roster;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.session.ISession;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class RosterProcessor implements IXepProcessor<Iq, Roster> {
	
	@Dependency("roster.operator")
	private RosterOperator rosterOperator;

	@Override
	public void process(IProcessingContext context, Iq iq, Roster roster) {
		JabberId userJid = context.getAttribute(ISession.KEY_SESSION_JID);
		
		if (iq.getType() == Iq.Type.SET) {
			rosterOperator.rosterSet(context, userJid, roster);
			rosterOperator.reply(context, userJid, iq.getId());
		} else if (iq.getType() == Iq.Type.GET) {
			rosterOperator.rosterGet(context, userJid, iq.getId());
		} else {
			throw new ProtocolException(new BadRequest("Roster result not supported."));
		}
		
	}

}
