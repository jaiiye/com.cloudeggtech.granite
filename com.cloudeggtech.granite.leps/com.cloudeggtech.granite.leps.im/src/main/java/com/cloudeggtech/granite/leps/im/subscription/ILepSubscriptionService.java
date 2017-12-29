package com.cloudeggtech.granite.leps.im.subscription;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.im.ISubscriptionService;
import com.cloudeggtech.granite.framework.im.Subscription;
import com.cloudeggtech.granite.framework.im.SubscriptionNotification;

public interface ILepSubscriptionService extends ISubscriptionService {
	boolean notificationExists(String notificationId);
	SubscriptionNotification getNotificationById(String notificationId);
	void removeNotificationById(String notificationId);
	void updateStates(JabberId user, JabberId contact, Subscription.State state);
	void updateNotification(SubscriptionNotification existed);
}
