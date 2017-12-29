package com.cloudeggtech.granite.lite.im;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.ProtocolException;
import com.cloudeggtech.basalt.protocol.core.stanza.error.InternalServerError;
import com.cloudeggtech.granite.framework.core.plumbing.persistent.IPersistentObjectFactory;
import com.cloudeggtech.granite.framework.core.plumbing.persistent.IPersistentObjectFactoryAware;
import com.cloudeggtech.granite.framework.im.ISubscriptionService;
import com.cloudeggtech.granite.framework.im.Subscription;
import com.cloudeggtech.granite.framework.im.Subscription.State;
import com.cloudeggtech.granite.framework.im.SubscriptionChanges;
import com.cloudeggtech.granite.framework.im.SubscriptionNotification;
import com.cloudeggtech.granite.framework.im.SubscriptionType;

@Transactional
@Component
public class SubscriptionService implements ISubscriptionService, IPersistentObjectFactoryAware {
	@Autowired
	private SqlSession sqlSession;
	
	private IPersistentObjectFactory persistentObjectFactory;
	
	@Override
	public List<Subscription> get(String user) {
		return getMapper().selectByUser(user);
	}

	@Override
	public Subscription get(String user, String contact) {
		return getMapper().selectByUserAndContact(user, contact);
	}

	@Override
	public boolean exists(String user, String contact) {
		return getMapper().selectCountByUserAndContact(user, contact) != 0;
	}

	private SubscriptionMapper getMapper() {
		return sqlSession.getMapper(SubscriptionMapper.class);
	}

	@Override
	public void add(Subscription subscription) {
		getMapper().insert(subscription);
	}

	@Override
	public void updateNameAndGroups(String user, String contact, String name, String groups) {
		getMapper().updateNameAndGroups(user, contact, name, groups);
	}
	
	@Override
	public void updateState(String user, String contact, Subscription.State state) {
		getMapper().updateState(user, contact, state);
	}
	
	@Override
	public SubscriptionChanges handleSubscription(JabberId user, JabberId contact,
			SubscriptionType subscriptionType) {
		SubscriptionChange userSubscriptionChange = handleOutboundSubscription(user, contact, subscriptionType);
		SubscriptionChange contactSubscriptionChange = handleInboundSubscription(contact, user, subscriptionType);
		
		return new SubscriptionChanges(
				userSubscriptionChange == null ? null : userSubscriptionChange.oldState,
				userSubscriptionChange == null ? null : userSubscriptionChange.subscription,
				contactSubscriptionChange == null ? null : contactSubscriptionChange.oldState,
				contactSubscriptionChange == null ? null : contactSubscriptionChange.subscription
		);
	}
	
	private class SubscriptionChange {
		public Subscription.State oldState;
		public Subscription subscription;
	}
	
	private SubscriptionChange handleOutboundSubscription(JabberId user, JabberId contact,
			SubscriptionType subscriptionType) {
		Subscription subscription = get(user.getName(), contact.getBareIdString());
		if (subscription == null) {
			throw new ProtocolException(new InternalServerError("null subscription state. roster set first"));
		}
		
		Subscription.State oldState = subscription.getState();
		Subscription.State newState = getOutboundSubscriptionNewState(oldState, subscriptionType);
		
		if (newState == oldState)
			return null;
		
		subscription.setState(newState);
		updateState(user.getName(), contact.getBareIdString(), newState);
		
		SubscriptionChange change = new SubscriptionChange();
		change.oldState = oldState;
		change.subscription = subscription;
		
		return change;
	}

	private State getOutboundSubscriptionNewState(State oldState, SubscriptionType subscriptionType) {
		State newState;
		
		if (subscriptionType == SubscriptionType.SUBSCRIBE) {
			if (oldState == State.NONE) {
				newState = State.NONE_PENDING_OUT;
			} else if (oldState == State.NONE_PENDING_IN) {
				newState = State.NONE_PENDING_IN_OUT;
			} else if (oldState == State.FROM) {
				newState = State.FROM_PENDING_OUT;
			} else {
				newState = oldState;
			}
		} else if (subscriptionType == SubscriptionType.UNSUBSCRIBE) {
			if (oldState == State.NONE_PENDING_OUT) {
				newState = State.NONE;
			} else if (oldState == State.NONE_PENDING_IN_OUT) {
				newState = State.NONE_PENDING_IN;
			} else if (oldState == State.TO) {
				newState = State.NONE;
			} else if (oldState == State.TO_PENDING_IN) {
				newState = State.NONE_PENDING_IN;
			} else if (oldState == State.FROM_PENDING_OUT) {
				newState = State.FROM;
			} else if (oldState == State.BOTH) {
				newState = State.FROM;
			} else {
				newState = oldState;
			}
		} else if (subscriptionType == SubscriptionType.SUBSCRIBED) {
			if (oldState == State.NONE_PENDING_IN) {
				newState = State.FROM;
			} else if (oldState == State.NONE_PENDING_IN_OUT) {
				newState = State.FROM_PENDING_OUT;
			} else if (oldState == State.TO_PENDING_IN) {
				newState = State.BOTH;
			} else {
				newState = oldState;
			}
		} else { // subscriptionType == SubscriptionType.UNSUBSCRIBED
			if (oldState == State.NONE_PENDING_IN) {
				newState = State.NONE;
			} else if (oldState == State.NONE_PENDING_IN_OUT) {
				newState = State.NONE_PENDING_OUT;
			} else if (oldState == State.TO_PENDING_IN) {
				newState = State.TO;
			} else if (oldState == State.FROM) {
				newState = State.NONE;
			} else if (oldState == State.FROM_PENDING_OUT) {
				newState = State.NONE_PENDING_OUT;
			} else if (oldState == State.BOTH) {
				newState = State.TO;
			} else {
				newState = oldState;
			}
		}
		
		return newState;
	}

	private SubscriptionChange handleInboundSubscription(JabberId user, JabberId contact, SubscriptionType subscriptionType) {
		boolean subscriptionExist = true;
		Subscription subscription = get(user.getName(), contact.getBareIdString());
		
		if (subscription == null) {
			subscriptionExist = false;
			subscription = persistentObjectFactory.create(Subscription.class);
			subscription.setUser(user.getName());
			subscription.setContact(contact.getBareIdString());
			subscription.setState(Subscription.State.NONE);
		}
		
		Subscription.State oldState = subscription.getState();
		Subscription.State newState = getInboundSubscriptionNewState(oldState, subscriptionType);
		
		if (newState == oldState)
			return null;
		
		subscription.setState(newState);
		if (subscriptionExist) {
			updateState(user.getName(), contact.getBareIdString(), newState);
		} else {
			add(subscription);
		}
		
		SubscriptionChange change = new SubscriptionChange();
		change.oldState = oldState;
		change.subscription = subscription;
		
		return change;
	}

	private State getInboundSubscriptionNewState(State oldState, SubscriptionType subscriptionType) {
		State newState;
		
		if (subscriptionType == SubscriptionType.SUBSCRIBE) {
			if (oldState == State.NONE) {
				newState = State.NONE_PENDING_IN;
			} else if (oldState == State.NONE_PENDING_OUT) {
				newState = State.NONE_PENDING_IN_OUT;
			} else if (oldState == State.TO) {
				newState = State.TO_PENDING_IN;
			} else {
				newState = oldState;
			}
		} else if (subscriptionType == SubscriptionType.UNSUBSCRIBE) {
			if (oldState == State.NONE_PENDING_IN) {
				newState = State.NONE;
			} else if (oldState == State.NONE_PENDING_IN_OUT) {
				newState = State.NONE_PENDING_OUT;
			} else if (oldState == State.TO_PENDING_IN) {
				newState = State.TO;
			} else if (oldState == State.FROM) {
				newState = State.NONE;
			} else if (oldState == State.FROM_PENDING_OUT) {
				newState = State.NONE_PENDING_OUT;
			} else if (oldState == State.BOTH) {
				newState = State.TO;
			} else {
				newState = oldState;
			}
		} else if (subscriptionType == SubscriptionType.SUBSCRIBED) {
			if (oldState == State.NONE_PENDING_OUT) {
				newState = State.TO;
			} else if (oldState == State.NONE_PENDING_IN_OUT) {
				newState = State.TO_PENDING_IN;
			} else if (oldState == State.FROM_PENDING_OUT) {
				newState = State.BOTH;
			} else {
				newState = oldState;
			}
		} else { // subscriptionType == SubscriptionType.UNSUBSCRIBED
			if (oldState == State.NONE_PENDING_OUT) {
				newState = State.NONE;
			} else if (oldState == State.NONE_PENDING_IN_OUT) {
				newState = State.NONE_PENDING_IN;
			} else if (oldState == State.TO) {
				newState = State.NONE;
			} else if (oldState == State.TO_PENDING_IN) {
				newState = State.NONE_PENDING_IN;
			} else if (oldState == State.FROM_PENDING_OUT) {
				newState = State.FROM;
			} else if (oldState == State.BOTH) {
				newState = State.FROM;
			} else {
				newState = oldState;
			}
		}
		
		return newState;
	}

	@Override
	public void remove(String user, String contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SubscriptionNotification> getNotificationsByUser(String user) {
		return getMapper().selectNotificationsByUser(user);
	}
	
	@Override
	public List<SubscriptionNotification> getNotificationsByUserAndContact(String user, String contact) {
		return getMapper().selectNotificationsByUserAndContact(user, contact);
	}

	@Override
	public void addNotification(SubscriptionNotification notification) {
		getMapper().insertNotification(notification);
	}

	@Override
	public void removeNotification(SubscriptionNotification notification) {
		getMapper().deleteNotification(notification);
	}

	@Override
	public void setPersistentObjectFactory(IPersistentObjectFactory persistentObjectFactory) {
		this.persistentObjectFactory = persistentObjectFactory;
	}
}
