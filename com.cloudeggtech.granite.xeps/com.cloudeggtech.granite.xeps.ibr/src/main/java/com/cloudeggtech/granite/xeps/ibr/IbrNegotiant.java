package com.cloudeggtech.granite.xeps.ibr;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.cloudeggtech.basalt.protocol.core.IError;
import com.cloudeggtech.basalt.protocol.core.ProtocolChain;
import com.cloudeggtech.basalt.protocol.core.ProtocolException;
import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.protocol.core.stanza.error.BadRequest;
import com.cloudeggtech.basalt.protocol.core.stanza.error.Forbidden;
import com.cloudeggtech.basalt.protocol.core.stanza.error.StanzaError;
import com.cloudeggtech.basalt.protocol.core.stream.Feature;
import com.cloudeggtech.basalt.protocol.core.stream.Stream;
import com.cloudeggtech.basalt.protocol.core.stream.error.ConnectionTimeout;
import com.cloudeggtech.basalt.protocol.core.stream.error.StreamError;
import com.cloudeggtech.basalt.protocol.oxm.OxmService;
import com.cloudeggtech.basalt.protocol.oxm.convention.NamingConventionParserFactory;
import com.cloudeggtech.basalt.protocol.oxm.convention.NamingConventionTranslatorFactory;
import com.cloudeggtech.basalt.protocol.oxm.parsers.core.stanza.IqParserFactory;
import com.cloudeggtech.basalt.protocol.oxm.parsing.IParsingFactory;
import com.cloudeggtech.basalt.protocol.oxm.translating.ITranslatingFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.core.stanza.IqTranslatorFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.core.stream.StreamTranslatorFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.error.StreamErrorTranslatorFactory;
import com.cloudeggtech.basalt.xeps.ibr.IqRegister;
import com.cloudeggtech.basalt.xeps.ibr.RegistrationForm;
import com.cloudeggtech.basalt.xeps.ibr.oxm.IqRegisterParserFactory;
import com.cloudeggtech.basalt.xeps.ibr.oxm.IqRegisterTranslatorFactory;
import com.cloudeggtech.basalt.xeps.oob.XOob;
import com.cloudeggtech.basalt.xeps.xdata.XData;
import com.cloudeggtech.granite.framework.core.connection.IClientConnectionContext;
import com.cloudeggtech.granite.framework.core.integration.IMessage;
import com.cloudeggtech.granite.framework.stream.negotiants.InitialStreamNegotiant;

public class IbrNegotiant extends InitialStreamNegotiant {
	public static final Object KEY_IBR_REGISTERED = new Object();
	
	private static final IParsingFactory parsingFactory = OxmService.createParsingFactory();
	private static final ITranslatingFactory translatingFactory = OxmService.createTranslatingFactory();
	
	private static final Object ATTRIBUTE_KEY_TIMEOUT_TIME = new Object();
	
	private static final long DEFAULT_REGISTER_TIMEOUT_TIME = 30 * 1000; // 30 seconds
	
	static {
		parsingFactory.register(
				ProtocolChain.first(Iq.PROTOCOL),
				new IqParserFactory()
		);
		parsingFactory.register(
				ProtocolChain.first(Iq.PROTOCOL).
					next(IqRegister.PROTOCOL),
				new IqRegisterParserFactory()
		);
		parsingFactory.register(
				ProtocolChain.first(Iq.PROTOCOL).
					next(IqRegister.PROTOCOL).
					next(XData.PROTOCOL),
				new NamingConventionParserFactory<>(XData.class)
		);
		parsingFactory.register(
				ProtocolChain.first(Iq.PROTOCOL).
					next(IqRegister.PROTOCOL).
					next(XOob.PROTOCOL),
				new NamingConventionParserFactory<>(XOob.class)
		);
		
		translatingFactory.register(
				Iq.class,
				new IqTranslatorFactory()
		);
		translatingFactory.register(
				IqRegister.class,
				new IqRegisterTranslatorFactory()
		);
		translatingFactory.register(
				XData.class,
				new NamingConventionTranslatorFactory<>(XData.class)
		);
		translatingFactory.register(
				XOob.class,
				new NamingConventionTranslatorFactory<>(XOob.class)
		);
		translatingFactory.register(StreamError.class, new StreamErrorTranslatorFactory());
		translatingFactory.register(Stream.class, new StreamTranslatorFactory());
	}
	
	private enum State {
		NONE,
		FORM_REQUESTED,
		REGISTERED
	}
	
	private IRegistrar registrar;
	private State state;
	
	private long registerTimeoutTime;
	
	private Timer timeoutTimer;
	
	private int timeoutCheckInterval;
	
	public IbrNegotiant(String domainName, List<Feature> features, IRegistrar registrar,
			int timeoutCheckInterval) {
		super(domainName, features);
		
		this.registrar = registrar;
		this.timeoutCheckInterval = timeoutCheckInterval;
		registerTimeoutTime = DEFAULT_REGISTER_TIMEOUT_TIME;
		state = State.NONE;
	}
	
	protected boolean doNegotiate(IClientConnectionContext context, IMessage message) {
		if (context.getAttribute(IbrNegotiant.KEY_IBR_REGISTERED) != null) {
			if (next != null) {
				done = true;
				return next.negotiate(context, message);
			}
			
			throw new ProtocolException(new BadRequest("Stream has estabilished."));
		}
		
		if (state == State.NONE) {
			Iq iq = null;
			try {
				iq = (Iq)parsingFactory.parse((String)message.getPayload());
			} catch (Exception e) {
				// ignore
			}
			
			if (iq == null || (!isRegistrationFormRequest(iq) && !isRegisterRequest((IqRegister)iq.getObject()))) {
				if (next != null) {
					done = true;
					return next.negotiate(context, message);
				}
				
				throw new ProtocolException(new BadRequest("Stream has estabilished."));
			}
		}
		
		try {
			if (state != State.REGISTERED) {
				negotiateIbr(context, message);				
				return false;
			} else {
				return super.doNegotiate(context, message);
			}
		} catch (ProtocolException e) {
			if (e.getError() instanceof StanzaError) {
				updateTimeoutTime(context);
			}
			
			throw e;
		} catch (RuntimeException e) {
			timeoutTimer.cancel();
			throw e;
		}
		
	}

	private boolean negotiateIbr(final IClientConnectionContext context, IMessage message) {
		Iq iq = (Iq)parsingFactory.parse((String)message.getPayload());
		IqRegister iqRegister = iq.getObject();
		
		if (iqRegister == null) {
			throw new ProtocolException(new BadRequest());
		}
		
		try {
			return doNegotiateIbr(context, iq, iqRegister);
		} catch (ProtocolException e) {
			IError error = e.getError();
			
			if (error instanceof StanzaError) {
				((StanzaError)error).setId(iq.getId());
			}
			
			throw e;
		}
		
	}

	private boolean doNegotiateIbr(final IClientConnectionContext context, Iq iq, IqRegister iqRegister) {
		if (isRegistrationFormRequest(iq)) {
			if (state != State.NONE) {
				throw new ProtocolException(new Forbidden(String.format("Illegal IBR state[%s].", state)));
			}
			
			returnRegistrationForm(context, iq.getId());
			
			state = State.FORM_REQUESTED;
			updateTimeoutTime(context);
			
			startTimer(context);
			
			return false;
		} else if (isRegisterRequest(iqRegister)) {
			if (state != State.NONE && state != State.FORM_REQUESTED) {
				throw new ProtocolException(new Forbidden(String.format("Illegal IBR state[%s].", state)));
			}
			
			register(context, iq.getId(), iqRegister);
			
			return false;
		} else {
			throw new ProtocolException(new BadRequest("Illegal IBR request."));
		}
	}

	private void startTimer(final IClientConnectionContext context) {
		timeoutTimer = new Timer();
		timeoutTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Long timeoutTime = context.getAttribute(ATTRIBUTE_KEY_TIMEOUT_TIME);
				
				if (timeoutTime != null) {
					long currentTime = System.currentTimeMillis();
					
					if (currentTime > timeoutTime) {
						context.write(translatingFactory.translate(new ConnectionTimeout()));
						context.write(translatingFactory.translate(new Stream(true)));
						context.close();
						
						timeoutTimer.cancel();
					}
				}
			}
			
		}, timeoutCheckInterval, timeoutCheckInterval);
	}

	private void updateTimeoutTime(IClientConnectionContext context) {
		context.setAttribute(ATTRIBUTE_KEY_TIMEOUT_TIME, registerTimeoutTime + System.currentTimeMillis());
	}

	private void register(IClientConnectionContext context, String requestId, IqRegister iqRegister) {
		registrar.register(iqRegister);
		
		state = State.REGISTERED;
		
		context.setAttribute(IbrNegotiant.KEY_IBR_REGISTERED, new Object());
		
		Iq result = new Iq(Iq.Type.RESULT, requestId);
		context.write(translatingFactory.translate(result));
	}

	private boolean isRegisterRequest(IqRegister iqRegister) {
		if (iqRegister.getRegister() != null && iqRegister.getRegister() instanceof RegistrationForm)
			return true;
		
		if (iqRegister.getXData() != null)
			return true;
		
		return false;
	}

	private void returnRegistrationForm(IClientConnectionContext context, String requestId) {
		IqRegister iqRegister = registrar.getRegistrationForm();
		Iq iq = new Iq(Iq.Type.RESULT, requestId);
		iq.setObject(iqRegister);
		
		context.write(translatingFactory.translate(iq));
	}

	private boolean isRegistrationFormRequest(Iq iq) {
		if (iq == null)
			return false;
		
		if (iq.getObject() instanceof IqRegister) {
			IqRegister iqRegister = (IqRegister)iq.getObject();
			if (iqRegister.getRegister() != null ||
					iqRegister.getXData() != null ||
						iqRegister.getXData() != null) {
				return false;
			}
		}
		
		return true;
	}
	
	public void setRegisterTimeoutTime(long registerTimeoutTime) {
		this.registerTimeoutTime = registerTimeoutTime;
	}

}
