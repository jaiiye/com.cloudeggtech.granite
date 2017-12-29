package com.cloudeggtech.granite.framework.parsing;

import com.cloudeggtech.basalt.protocol.core.ProtocolChain;
import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;
import com.cloudeggtech.basalt.protocol.oxm.parsers.im.MessageParserFactory;
import com.cloudeggtech.basalt.protocol.oxm.parsers.im.PresenceParserFactory;
import com.cloudeggtech.granite.framework.core.annotations.Component;

@Component("default.message.parsing.processor")
public class DefaultMessageParsingProcessor extends MinimumMessageParsingProcessor {
	
	public DefaultMessageParsingProcessor() {
		super("Basalt-Parsers", "Granite-Pipe-Preprocessors");
	}
	
	@Override
	protected void registerPredefinedParsers() {
		super.registerPredefinedParsers();
		
		parsingFactory.register(ProtocolChain.first(Presence.PROTOCOL), new PresenceParserFactory());
		parsingFactory.register(ProtocolChain.first(Message.PROTOCOL), new MessageParserFactory());
	}
	
}
