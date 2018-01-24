package com.cloudeggtech.granite.stream.standard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.LangText;
import com.cloudeggtech.basalt.protocol.core.ProtocolChain;
import com.cloudeggtech.basalt.protocol.core.ProtocolException;
import com.cloudeggtech.basalt.protocol.core.stanza.error.StanzaError;
import com.cloudeggtech.basalt.protocol.core.stream.Bind;
import com.cloudeggtech.basalt.protocol.core.stream.Feature;
import com.cloudeggtech.basalt.protocol.core.stream.Session;
import com.cloudeggtech.basalt.protocol.core.stream.Stream;
import com.cloudeggtech.basalt.protocol.core.stream.error.InternalServerError;
import com.cloudeggtech.basalt.protocol.core.stream.error.StreamError;
import com.cloudeggtech.basalt.protocol.core.stream.sasl.Mechanisms;
import com.cloudeggtech.basalt.protocol.core.stream.tls.StartTls;
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
import com.cloudeggtech.granite.framework.core.auth.IAuthenticator;
import com.cloudeggtech.granite.framework.core.commons.osgi.IBundleContextAware;
import com.cloudeggtech.granite.framework.core.commons.osgi.IContributionTracker;
import com.cloudeggtech.granite.framework.core.commons.osgi.OsgiUtils;
import com.cloudeggtech.granite.framework.core.commons.utils.CommonUtils;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfiguration;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfigurationAware;
import com.cloudeggtech.granite.framework.core.config.IConfiguration;
import com.cloudeggtech.granite.framework.core.config.IConfigurationAware;
import com.cloudeggtech.granite.framework.core.connection.IClientConnectionContext;
import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;
import com.cloudeggtech.granite.framework.core.connection.IConnectionManager;
import com.cloudeggtech.granite.framework.core.connection.IConnectionManagerAware;
import com.cloudeggtech.granite.framework.core.event.ConnectionClosedEvent;
import com.cloudeggtech.granite.framework.core.event.ConnectionOpenedEvent;
import com.cloudeggtech.granite.framework.core.integration.IClientMessageProcessor;
import com.cloudeggtech.granite.framework.core.integration.IMessage;
import com.cloudeggtech.granite.framework.core.integration.IMessageChannel;
import com.cloudeggtech.granite.framework.core.integration.SimpleMessage;
import com.cloudeggtech.granite.framework.core.plumbing.IApplicationComponentService;
import com.cloudeggtech.granite.framework.core.repository.IInitializable;
import com.cloudeggtech.granite.framework.core.routing.IRouter;
import com.cloudeggtech.granite.framework.core.session.ISessionListener;
import com.cloudeggtech.granite.framework.core.session.ISessionManager;
import com.cloudeggtech.granite.framework.stream.IStreamNegotiant;
import com.cloudeggtech.granite.framework.stream.StreamConstants;
import com.cloudeggtech.granite.framework.stream.negotiants.InitialStreamNegotiant;
import com.cloudeggtech.granite.framework.stream.negotiants.ResourceBindingNegotiant;
import com.cloudeggtech.granite.framework.stream.negotiants.SaslNegotiant;
import com.cloudeggtech.granite.framework.stream.negotiants.SessionEstablishmentNegotiant;
import com.cloudeggtech.granite.framework.stream.negotiants.TlsNegotiant;

@Component("standard.client.message.processor")
public class StandardClientMessageProcessor implements IClientMessageProcessor, IConfigurationAware,
		IApplicationConfigurationAware, IBundleContextAware, IInitializable {
	private static final Logger logger = LoggerFactory.getLogger(StandardClientMessageProcessor.class);
	
	private static final String CONFIGURATION_KEY_TLS_REQUIRED = "tls.required";
	private static final String CONFIGURATION_KEY_SASL_FAILURE_RETRIES = "sasl.failure.retries";
	private static final String CONFIGURATION_KEY_SASL_ABORT_RETRIES = "sasl.abort.retries";
	private static final String CONFIGURATION_KEY_SASL_SUPPORTED_MECHANISMS = "sasl.supported.mechanisms";
	
	private static final String KEY_GRANITE_SESSION_LISTENERS = "Granite-Session-Listeners";

	private static final Object KEY_NEGOTIANT = "granite.key.negotiant";
	
	private IConnectionManager connectionManager;

	private IParsingFactory parsingFactory;
	private ITranslatingFactory translatingFactory;
	
	protected IAuthenticator authenticator;	
	protected ISessionManager sessionManager;
	protected IMessageChannel messageChannel;
	protected IMessageChannel eventMessageChannel;
	protected IRouter router;
	
	protected String hostName;
	
	protected boolean tlsRequired;
	
	protected int saslAbortRetries;
	protected int saslFailureRetries;
	protected String[] saslSupportedMechanisms;
	
	protected BundleContext bundleContext;
	
	protected ISessionListener sessionListenerDelegate;
	
	private Map<String, List<ISessionListener>> bundleAndSessionListeners;
	
	private volatile ISessionListener[] sessionListeners;
	
	private IApplicationComponentService appComponentService;
	
	public StandardClientMessageProcessor() {
		parsingFactory = OxmService.createParsingFactory();
		parsingFactory.register(ProtocolChain.first(Stream.PROTOCOL), new AnnotatedParserFactory<>(StreamParser.class));
		
		translatingFactory = OxmService.createTranslatingFactory();
		translatingFactory.register(Stream.class, new StreamTranslatorFactory());
		translatingFactory.register(StreamError.class, new StreamErrorTranslatorFactory());
		translatingFactory.register(StanzaError.class, new StanzaErrorTranslatorFactory());
		
		bundleAndSessionListeners = new HashMap<>();
		sessionListenerDelegate = new SessionListenerDelegate();
	}
	
	private class SessionListenerDelegate implements ISessionListener {

		@Override
		public void sessionEstablishing(IConnectionContext context, JabberId sessionJid) throws Exception {
			ISessionListener[] sessionListeners = getSessionListeners();
			
			if (sessionListeners == null || sessionListeners.length == 0)
				return;
			
			for (ISessionListener sessionListener : sessionListeners) {
				sessionListener.sessionEstablishing(context, sessionJid);
			}
		}
		
		@Override
		public void sessionEstablished(IConnectionContext context, JabberId sessionJid) throws Exception {
			ISessionListener[] sessionListeners = getSessionListeners();
			
			if (sessionListeners == null || sessionListeners.length == 0)
				return;
			
			for (ISessionListener sessionListener : sessionListeners) {
				sessionListener.sessionEstablished(context, sessionJid);
			}
		}

		@Override
		public void sessionClosing(IConnectionContext context, JabberId sessionJid) throws Exception {
			ISessionListener[] sessionListeners = getSessionListeners();
			
			if (sessionListeners == null || sessionListeners.length == 0)
				return;
			
			for (ISessionListener sessionListener : sessionListeners) {
				sessionListener.sessionClosing(context, sessionJid);
			}
		}

		@Override
		public void sessionClosed(IConnectionContext context, JabberId sessionJid) throws Exception {
			ISessionListener[] sessionListeners = getSessionListeners();
			
			if (sessionListeners == null || sessionListeners.length == 0)
				return;
			
			for (ISessionListener sessionListener : sessionListeners) {
				sessionListener.sessionClosed(context, sessionJid);
			}
		}
		
	}

	@Override
	public void process(IConnectionContext context, IMessage message) {
		doProcess((IClientConnectionContext)context, message);
		
	}
	
	public ISessionListener[] getSessionListeners() {
		if (sessionListeners != null) {
			return sessionListeners;
		}
		
		synchronized (this) {
			if (sessionListeners != null)
				return sessionListeners;
			
			List<ISessionListener> allBundlesSessionListeners = new ArrayList<>();
			for (List<ISessionListener> bundleSessionListeners : bundleAndSessionListeners.values()) {
				allBundlesSessionListeners.addAll(bundleSessionListeners);
			}
			
			sessionListeners = allBundlesSessionListeners.toArray(new ISessionListener[allBundlesSessionListeners.size()]);
			
			return sessionListeners;
		}
	}

	private void doProcess(IClientConnectionContext context, IMessage message) {
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
			IStreamNegotiant negotiant = context.getAttribute(KEY_NEGOTIANT);
			if (negotiant == null) {
				negotiant = createNegotiant();
				context.setAttribute(KEY_NEGOTIANT, negotiant);
			}
			
			try {
				if (negotiant.negotiate(context, message)) {
					context.removeAttribute(KEY_NEGOTIANT);
				}
			} catch (ProtocolException e) {
				context.write(translatingFactory.translate(e.getError()));
				if (e.getError() instanceof StreamError) {
					closeStream(context);
				}
			} catch (RuntimeException e) {
				logger.warn("Negotiation error.", e);
				
				InternalServerError error = new InternalServerError();
				error.setText(new LangText(String.format("Negotiation error. %s.",
						CommonUtils.getInternalServerErrorMessage(e))));
				context.write(translatingFactory.translate(error));
				closeStream(context);
			}
			
		}
	}

	private void fireConnectionOpenedEvent(IClientConnectionContext context) {
		ConnectionOpenedEvent event = new ConnectionOpenedEvent(context.getConnectionId().toString(),
				context.getRemoteIp(), context.getRemotePort());	
		eventMessageChannel.send(new SimpleMessage(event));
	}
	
	private void fireConnectionClosedEvent(IClientConnectionContext context) {
		ConnectionClosedEvent event = new ConnectionClosedEvent(context.getConnectionId().toString(),
				context.getJid(), context.getStreamId());	
		eventMessageChannel.send(new SimpleMessage(event));
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

	protected IStreamNegotiant createNegotiant() {
		IStreamNegotiant intialStream = new InitialStreamNegotiant(hostName,
				getInitialStreamNegotiantAdvertisements());
		
		IStreamNegotiant tls = new TlsNegotiant(hostName, tlsRequired,
				getTlsNegotiantAdvertisements());
		
		IStreamNegotiant sasl = new SaslNegotiant(hostName,
				saslSupportedMechanisms, saslAbortRetries, saslFailureRetries,
				getSaslNegotiantFeatures(), authenticator);
		
		IStreamNegotiant resourceBinding = new ResourceBindingNegotiant(
				hostName, sessionManager);
		IStreamNegotiant sessionEstablishment = new SessionEstablishmentNegotiant(
				router, sessionManager, eventMessageChannel, sessionListenerDelegate);
		
		resourceBinding.setNext(sessionEstablishment);
		sasl.setNext(resourceBinding);
		tls.setNext(sasl);
		intialStream.setNext(tls);
		
		return intialStream;
	}

	private String[] parseSupportedMechanisms(String sMechanisms) {
		StringTokenizer st = new StringTokenizer(sMechanisms, ",");
		
		if (st.countTokens() == 0) {
			throw new IllegalArgumentException(String.format("Can't determine supported sasl mechanisms: %s.", sMechanisms));
		}
		
		String[] mechanisms = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			mechanisms[i] = st.nextToken().trim();
			i++;
		}
		
		return mechanisms;
	}

	protected List<Feature> getSaslNegotiantFeatures() {
		List<Feature> features = new ArrayList<>();
		features.add(new Bind());
		features.add(new Session());
		
		return features;
	}

	protected List<Feature> getTlsNegotiantAdvertisements() {
		List<Feature> features = new ArrayList<>();
		
		Mechanisms mechanisms = new Mechanisms();
		for (String supportedMechanism : saslSupportedMechanisms) {
			mechanisms.getMechanisms().add(supportedMechanism);
		}
		
		features.add(mechanisms);
		
		return features;
	}

	protected List<Feature> getInitialStreamNegotiantAdvertisements() {
		List<Feature> features = new ArrayList<>();
		
		StartTls startTls = new StartTls();
		if (tlsRequired) {
			startTls.setRequired(true);
		}
		features.add(startTls);
		
		features.addAll(getTlsNegotiantAdvertisements());
		
		return features;
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		hostName = appConfiguration.getDomainName();
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		tlsRequired = configuration.getBoolean(CONFIGURATION_KEY_TLS_REQUIRED, true);
		
		saslSupportedMechanisms = parseSupportedMechanisms(configuration.getString(
				CONFIGURATION_KEY_SASL_SUPPORTED_MECHANISMS, "PLAIN,DIGEST-MD5"));
		saslAbortRetries = configuration.getInteger(CONFIGURATION_KEY_SASL_ABORT_RETRIES, 3);
		saslFailureRetries = configuration.getInteger(CONFIGURATION_KEY_SASL_FAILURE_RETRIES, 3);
	}

	@Override
	public void connectionOpened(IClientConnectionContext context) {
		fireConnectionOpenedEvent(context);
	}
	
	@Override
	public void connectionClosing(IClientConnectionContext context) {
		try {
			sessionListenerDelegate.sessionClosing(context, context.getJid());
		} catch (Exception e) {
			logger.error("Some errors occurred in session closing callback method.", e);
		}
	}

	@Override
	public void connectionClosed(IClientConnectionContext context, JabberId sessionJid) {
		try {
			sessionListenerDelegate.sessionClosed(context, sessionJid);
		} catch (Exception e) {
			logger.error("Some errors occurred in session closed callback method.", e);
		}
		
		fireConnectionClosedEvent(context);
	}
	
	@Dependency("authenticator")
	public void setAuthenticator(IAuthenticator authenticator) {
		this.authenticator = authenticator;
	}
	
	@Dependency("session.manager")
	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	@Dependency("message.channel")
	public void setMessageChannel(IMessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}
	
	@Dependency("event.message.channel")
	public void setEventMessageChannel(IMessageChannel eventMessageChannel) {
		this.eventMessageChannel = eventMessageChannel;
	}
	
	@Dependency("router")
	public void setRouter(IRouter router) {
		this.router = router;
	}

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
		
	}

	@Override
	public void init() {
		 appComponentService = OsgiUtils.getService(bundleContext, IApplicationComponentService.class);
		OsgiUtils.trackContribution(bundleContext, KEY_GRANITE_SESSION_LISTENERS, new SessionListenersContributionTracker());
	}
	
	private class SessionListenersContributionTracker implements IContributionTracker {

		@Override
		public void found(Bundle bundle, String contribution) throws Exception {
			StringTokenizer st = new StringTokenizer(contribution, ",");
			if (st.countTokens() == 0)
				return;
			
			List<ISessionListener> sessionListeners = new ArrayList<>();
			while (st.hasMoreTokens()) {
				String sType = st.nextToken();
				Class<?> type = bundle.loadClass(sType);
				
				if (!ISessionListener.class.isAssignableFrom(type)) {
					throw new RuntimeException(String.format("%s should implement %s interface.",
							sType, ISessionListener.class.getName()));
				}
				
				ISessionListener sessionListener = (ISessionListener)type.newInstance();
				
				appComponentService.inject(sessionListener, bundleContext);
				
				if (sessionListener instanceof IConnectionManagerAware) {
					((IConnectionManagerAware)sessionListener).setConnectionManager(connectionManager);
				}
				
				sessionListeners.add(sessionListener);
			}
			
			synchronized (StandardClientMessageProcessor.this) {
				bundleAndSessionListeners.put(bundle.getSymbolicName(), sessionListeners);
				sessionListeners = null;
			}
		}

		@Override
		public void lost(Bundle bundle, String contribution) throws Exception {
			synchronized (StandardClientMessageProcessor.this) {
				bundleAndSessionListeners.remove(bundle.getSymbolicName());
				sessionListeners = null;
			}
		}
		
	}

	@Override
	public void setConnectionManager(IConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

}
