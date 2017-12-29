package com.cloudeggtech.granite.framework.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.stream.Stream;
import com.cloudeggtech.basalt.protocol.core.stream.error.Conflict;
import com.cloudeggtech.basalt.protocol.oxm.IOxmFactory;
import com.cloudeggtech.basalt.protocol.oxm.OxmService;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.connection.IClientConnectionContext;
import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;
import com.cloudeggtech.granite.framework.core.connection.IConnectionManager;
import com.cloudeggtech.granite.framework.core.connection.IConnectionManagerAware;
import com.cloudeggtech.granite.framework.core.session.ISessionListener;

public class SessionListener implements ISessionListener, IConnectionManagerAware {
	private static final Logger logger = LoggerFactory.getLogger(SessionListener.class);
	
	@Dependency("resources.register")
	private IResourcesRegister register;
	
	@Dependency("resources.service")
	private IResourcesService resourceService;
	
	private IConnectionManager connectionManager;
	
	private static final IOxmFactory oxmFactory = OxmService.createMinimumOxmFactory();
	
	private static final String MESSAGE_CONFLICT = oxmFactory.translate(new Conflict());
	private static final String MESSAGE_CLOSE_STREAM = oxmFactory.translate(new Stream(true));

	@Override
	public void sessionEstablished(IConnectionContext context, JabberId sessionJid) {
		try {
			register.register(sessionJid);
		} catch (RuntimeException e) {
			logger.error("Can't register resource. JID is {}.", sessionJid);
			throw e;
		}
		
	}

	@Override
	public void sessionClosing(IConnectionContext context, JabberId sessionJid) {}
	
	@Override
	public void sessionClosed(IConnectionContext context, JabberId sessionJid) {
		register.unregister(sessionJid);
	}

	@Override
	public void sessionEstablishing(IConnectionContext context, JabberId sessionJid) {
		if (resourceService.getResource(sessionJid) != null) {
			IClientConnectionContext clientContext = (IClientConnectionContext)connectionManager.
					getConnectionContext(sessionJid);
			
			if (clientContext != null) {
				clientContext.write(MESSAGE_CONFLICT, true);
				clientContext.write(MESSAGE_CLOSE_STREAM, true);
				
				clientContext.close(true);
			}
		}
	}

	@Override
	public void setConnectionManager(IConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

}
