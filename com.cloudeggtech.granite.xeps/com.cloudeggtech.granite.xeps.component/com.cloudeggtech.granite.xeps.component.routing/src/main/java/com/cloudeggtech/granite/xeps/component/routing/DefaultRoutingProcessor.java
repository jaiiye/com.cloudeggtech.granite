package com.cloudeggtech.granite.xeps.component.routing;

import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.basalt.protocol.oxm.translators.im.MessageTranslatorFactory;
import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.routing.MinimumRoutingProcessor;

@Component("default.component.routing.processor")
public class DefaultRoutingProcessor extends MinimumRoutingProcessor {
	
	public DefaultRoutingProcessor() {
		super("Basalt-Component-Translators", "Granite-Component-Pipe-Postprocessor");
	}
	
	@Override
	protected void registerPredefinedTranslators() {
		super.registerPredefinedTranslators();
		
		translatingFactory.register(Message.class, new MessageTranslatorFactory());
	}
}
