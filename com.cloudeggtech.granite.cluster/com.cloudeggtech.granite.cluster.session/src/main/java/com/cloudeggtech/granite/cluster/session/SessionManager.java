package com.cloudeggtech.granite.cluster.session;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.core.annotations.Component;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.repository.IInitializable;
import com.cloudeggtech.granite.framework.core.session.ISession;
import com.cloudeggtech.granite.framework.core.session.ISessionManager;
import com.cloudeggtech.granite.framework.core.session.SessionExistsException;

@Component("cluster.session.manager")
public class SessionManager implements ISessionManager, IInitializable {
	
	@Dependency("ignite")
	private Ignite ignite;
	
	private SessionsWrapper sessionsWrapper;
	
	@Override
	public void init() {
		sessionsWrapper = new SessionsWrapper(ignite);
	}
	
	@Override
	public ISession create(JabberId jid) throws SessionExistsException {
		ISession session = new Session(jid);
		if (!getSessions().putIfAbsent(jid, session)) {
			throw new SessionExistsException();
		}
		
		return session;
	}

	@Override
	public ISession get(JabberId jid) {
		return getSessions().get(jid);
	}

	@Override
	public boolean exists(JabberId jid) {
		return getSessions().containsKey(jid);
	}

	@Override
	public boolean remove(JabberId jid) {
		return getSessions().remove(jid);
	}
	
	private class SessionsWrapper {
		private Ignite ignite;
		private volatile IgniteCache<JabberId, ISession> sessions;
		
		public SessionsWrapper(Ignite ignite) {
			this.ignite = ignite;
		}
		
		public IgniteCache<JabberId, ISession> getSessions() {
			if (sessions != null) {
				return sessions;
			}
			
			synchronized(SessionManager.this) {
				if (sessions != null)
					return sessions;
				
				sessions = ignite.cache("sessions");
			}
			
			return sessions;
		}
	}
	
	private IgniteCache<JabberId, ISession> getSessions() {
		return sessionsWrapper.getSessions();
	}

	@Override
	public void put(JabberId jid, ISession session) {
		getSessions().put(jid, session);
	}
	
}
