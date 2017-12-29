package com.cloudeggtech.granite.xeps.disco;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;

public interface IDiscoProcessor {
	void discoInfo(IProcessingContext context, Iq iq, JabberId jid, String node);
	void discoItems(IProcessingContext context, Iq iq, JabberId jid, String node);
}
