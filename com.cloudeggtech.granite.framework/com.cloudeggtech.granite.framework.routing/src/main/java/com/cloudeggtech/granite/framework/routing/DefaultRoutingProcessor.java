package com.cloudeggtech.granite.framework.routing;

import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;
import com.cloudeggtech.basalt.protocol.oxm.translators.im.MessageTranslatorFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.im.PresenceTranslatorFactory;
import com.cloudeggtech.granite.framework.core.annotations.Component;

@Component("default.routing.processor")
public class DefaultRoutingProcessor extends MinimumRoutingProcessor {
	
	public DefaultRoutingProcessor() {
		super("Basalt-Translators", "Granite-Pipe-Postprocessors");
	}
	
	@Override
	protected void registerPredefinedTranslators() {
		super.registerPredefinedTranslators();
		
		translatingFactory.register(Presence.class, new PresenceTranslatorFactory());
		translatingFactory.register(Message.class, new MessageTranslatorFactory());
	}
}
