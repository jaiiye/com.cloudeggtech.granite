package com.cloudeggtech.granite.xeps.component.stream.accept;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.LangText;
import com.cloudeggtech.basalt.protocol.core.ProtocolChain;
import com.cloudeggtech.basalt.protocol.core.ProtocolException;
import com.cloudeggtech.basalt.protocol.core.stanza.error.StanzaError;
import com.cloudeggtech.basalt.protocol.core.stream.Stream;
import com.cloudeggtech.basalt.protocol.core.stream.error.InternalServerError;
import com.cloudeggtech.basalt.protocol.core.stream.error.StreamError;
import com.cloudeggtech.basalt.protocol.oxm.OxmService;
import com.cloudeggtech.basalt.protocol.oxm.annotation.AnnotatedParserFactory;
import com.cloudeggtech.basalt.protocol.oxm.parsers.core.stream.StreamParser;
import com.cloudeggtech.basalt.protocol.oxm.parsing.IParsingFactory;
import com.cloudeggtech.basalt.protocol.oxm.translating.ITranslatingFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.core.stream.StreamTranslatorFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.error.StanzaErrorTranslatorFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.error.StreamErrorTranslatorFactory;
import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.commons.utils.CommonUtils;
import com.cloudeggtech.granite.framework.core.commons.utils.IoUtils;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfiguration;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfigurationAware;
import com.cloudeggtech.granite.framework.core.connection.IClientConnectionContext;
import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;
import com.cloudeggtech.granite.framework.core.connection.IConnectionManager;
import com.cloudeggtech.granite.framework.core.connection.IConnectionManagerAware;
import com.cloudeggtech.granite.framework.core.integration.IMessage;
import com.cloudeggtech.granite.framework.core.integration.IMessageChannel;
import com.cloudeggtech.granite.framework.core.integration.SimpleMessage;
import com.cloudeggtech.granite.framework.stream.IStreamNegotiant;
import com.cloudeggtech.granite.framework.stream.StreamConstants;
import com.cloudeggtech.granite.xeps.component.stream.IComponentConnectionsRegister;
import com.cloudeggtech.granite.xeps.component.stream.IComponentMessageProcessor;
import com.cloudeggtech.granite.xeps.component.stream.accept.negotiants.HandshakeNegotiant;
import com.cloudeggtech.granite.xeps.component.stream.accept.negotiants.InitialStreamNegotiant;

@Component("component.accept.message.processor")
public class ComponentAcceptMessageProcessor implements IComponentMessageProcessor,
		IApplicationConfigurationAware, IConnectionManagerAware {
	private static final Logger logger = LoggerFactory.getLogger(ComponentAcceptMessageProcessor.class);
	
	private static final Object KEY_NEGOTIANT = "granite.key.negotiant";
	
	private IParsingFactory parsingFactory;
	private ITranslatingFactory translatingFactory;
	
	protected IMessageChannel messageChannel;
	
	private Map<String, String> components = new HashMap<>();
	
	private IComponentConnectionsRegister connectionsRegister;
	
	private IConnectionManager connectionManager;
	
	public ComponentAcceptMessageProcessor() {
		parsingFactory = OxmService.createParsingFactory();
		parsingFactory.register(ProtocolChain.first(Stream.PROTOCOL), new AnnotatedParserFactory<>(StreamParser.class));
		
		translatingFactory = OxmService.createTranslatingFactory();
		translatingFactory.register(Stream.class, new StreamTranslatorFactory());
		translatingFactory.register(StreamError.class, new StreamErrorTranslatorFactory());
		translatingFactory.register(StanzaError.class, new StanzaErrorTranslatorFactory());
	}

	@Override
	public void process(IConnectionContext context, IMessage message) {
		if (isCloseStreamRequest((String)message.getPayload())) {
			context.write(translatingFactory.translate(new Stream(true)));
			context.close();
			return;
		}
		
		JabberId jid = context.getAttribute(StreamConstants.KEY_SESSION_JID);
		if (jid != null) {
			Map<Object, Object> header = new HashMap<>();
			header.put(IMessage.KEY_SESSION_JID, jid);
			IMessage out = new SimpleMessage(header, message.getPayload());
			
			messageChannel.send(out);
		} else {
			IStreamNegotiant negotiant = (IStreamNegotiant)context.getAttribute(KEY_NEGOTIANT);
			if (negotiant == null) {
				negotiant = createNegotiant();
				context.setAttribute(KEY_NEGOTIANT, negotiant);
			}
			
			try {
				if (negotiant.negotiate((IClientConnectionContext)context, message)) {
					context.removeAttribute(KEY_NEGOTIANT);
				}
			} catch (ProtocolException e) {
				context.write(translatingFactory.translate(e.getError()));
				closeStream(context);
			} catch (RuntimeException e) {
				logger.warn("negotiation error", e);
				
				InternalServerError error = new InternalServerError();
				error.setText(new LangText(String.format("negotiation error. %s",
						CommonUtils.getInternalServerErrorMessage(e))));
				context.write(translatingFactory.translate(error));
				closeStream(context);
			}
			
		}
	}
	
	private IStreamNegotiant createNegotiant() {
		IStreamNegotiant initialStreamNegotiant = new InitialStreamNegotiant(components.keySet());
		initialStreamNegotiant.setNext(new HandshakeNegotiant(components, connectionManager, connectionsRegister));
		
		return initialStreamNegotiant;
	}

	private boolean isCloseStreamRequest(String message) {
		try {
			Object object = parsingFactory.parse(message, true);
			if (object instanceof Stream) {
				return ((Stream)object).getClose();
			}
			
			return false;			
		} catch (Exception e) {
			return false;
		}

	}

	private void closeStream(IConnectionContext context) {
		context.write(translatingFactory.translate(new Stream(true)));
		context.close();
	}
	
	@Dependency("message.channel")
	public void setMessageChannel(IMessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		File config = new File(appConfiguration.getConfigDir(), "jabber-component-protocol.ini");
		if (!config.exists()) {
			logger.warn("no jabber component configured. please define your jabber-component-protocol.ini file");
			return;
		}
		
		Properties properties = new Properties();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(config));
			properties.load(reader);
		} catch (Exception e) {
			throw new RuntimeException("can't reader jabber-component-protocol.ini", e);
		} finally {
			IoUtils.closeIO(reader);
		}
		
		for (Object component : properties.keySet()) {
			components.put((String)component, (String)properties.getProperty((String)component));
		}
	}

	@Override
	public void setComponentConnectionsRegister(IComponentConnectionsRegister connectionsRegister) {
		this.connectionsRegister = connectionsRegister;
	}

	@Override
	public void setConnectionManager(IConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
}
