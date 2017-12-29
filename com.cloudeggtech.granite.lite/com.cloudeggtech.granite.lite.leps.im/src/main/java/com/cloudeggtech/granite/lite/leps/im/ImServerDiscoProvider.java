package com.cloudeggtech.granite.lite.leps.im;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.xeps.disco.DiscoInfo;
import com.cloudeggtech.basalt.xeps.disco.DiscoItems;
import com.cloudeggtech.basalt.xeps.disco.Feature;
import com.cloudeggtech.basalt.xeps.disco.Identity;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfiguration;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfigurationAware;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.xeps.disco.IDiscoProvider;

public class ImServerDiscoProvider implements IDiscoProvider, IApplicationConfigurationAware {
	@Dependency("lep.im.server.listener")
	private LepImServerListener imServerListener;
	
	private JabberId serverJid;
	
	@Override
	public DiscoInfo discoInfo(IProcessingContext context, Iq iq, JabberId jid, String node) {
		if (!serverJid.equals(jid) || node != null)
			return null;
		
		DiscoInfo discoInfo = new DiscoInfo();
		
		discoInfo.getFeatures().add(new Feature("http://jabber.org/protocol/disco#info"));
		discoInfo.getFeatures().add(new Feature("http://jabber.org/protocol/disco#items"));
		
		if (imServerListener.isStandardStream()) {
			discoInfo.getFeatures().add(new Feature("jabber:client"));
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-streams"));
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-stanzas"));
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-tls"));
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-tls#c2s"));
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-sasl"));
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-sasl#c2s"));
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-bind"));
		}
		
		if (imServerListener.isIMServer()) {
			discoInfo.getIdentities().add(new Identity("server", "im(lep)"));
			
			discoInfo.getFeatures().add(new Feature("urn:ietf:params:xml:ns:xmpp-session"));
			discoInfo.getFeatures().add(new Feature("jabber:iq:roster"));
		}
		
		return discoInfo;
	}

	@Override
	public DiscoItems discoItems(IProcessingContext context, Iq iq, JabberId jid, String node) {
		return null;
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		serverJid = JabberId.parse(appConfiguration.getDomainName());
	}

}
