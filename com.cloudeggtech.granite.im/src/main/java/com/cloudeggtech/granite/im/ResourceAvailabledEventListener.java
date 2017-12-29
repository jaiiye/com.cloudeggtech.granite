package com.cloudeggtech.granite.im;

import java.util.List;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence;
import com.cloudeggtech.basalt.protocol.im.stanza.Presence.Type;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.event.IEventContext;
import com.cloudeggtech.granite.framework.core.event.IEventListener;
import com.cloudeggtech.granite.framework.im.ISubscriptionService;
import com.cloudeggtech.granite.framework.im.ResourceAvailabledEvent;
import com.cloudeggtech.granite.framework.im.SubscriptionNotification;
import com.cloudeggtech.granite.framework.im.SubscriptionType;

public class ResourceAvailabledEventListener implements IEventListener<ResourceAvailabledEvent> {
	@Dependency("subscription.service")
	private ISubscriptionService subscriptionService;

	@Override
	public void process(IEventContext context, ResourceAvailabledEvent event) {
		JabberId user = event.getJid();
		List<SubscriptionNotification> notifications = subscriptionService.getNotificationsByUser(user.getName());
		
		for (SubscriptionNotification notification : notifications) {
			Presence subscription = new Presence();
			subscription.setFrom(JabberId.parse(notification.getContact()));
			subscription.setTo(user);
			
			subscription.setType(subscriptionTypeToPresenceType(notification.getSubscriptionType()));
			
			context.write(subscription);
		}
	}

	private Type subscriptionTypeToPresenceType(SubscriptionType subscriptionType) {
		if (subscriptionType == SubscriptionType.SUBSCRIBE) {
			return Presence.Type.SUBSCRIBE;
		} else if (subscriptionType == SubscriptionType.UNSUBSCRIBE) {
			return Presence.Type.UNSUBSCRIBE;
		} else if (subscriptionType == SubscriptionType.SUBSCRIBED) {
			return Presence.Type.SUBSCRIBED;
		} else { // subscriptionType == SubscriptionTypee.UNSUBSCRIBED
			return Presence.Type.UNSUBSCRIBED;
		}
	}

}
