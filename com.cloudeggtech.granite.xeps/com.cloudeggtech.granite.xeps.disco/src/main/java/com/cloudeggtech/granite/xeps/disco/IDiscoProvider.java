package com.cloudeggtech.granite.xeps.disco;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.xeps.disco.DiscoInfo;
import com.cloudeggtech.basalt.xeps.disco.DiscoItems;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;

public interface IDiscoProvider {
	DiscoInfo discoInfo(IProcessingContext context, Iq iq, JabberId jid, String node);
	DiscoItems discoItems(IProcessingContext context, Iq iq, JabberId jid, String node);
}
