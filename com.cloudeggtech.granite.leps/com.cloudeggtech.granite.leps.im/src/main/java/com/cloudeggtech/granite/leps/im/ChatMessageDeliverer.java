package com.cloudeggtech.granite.leps.im;

import java.util.ArrayList;
import java.util.List;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.ProtocolException;
import com.cloudeggtech.basalt.protocol.core.stanza.Stanza;
import com.cloudeggtech.basalt.protocol.core.stanza.error.BadRequest;
import com.cloudeggtech.basalt.protocol.core.stanza.error.Forbidden;
import com.cloudeggtech.basalt.protocol.core.stream.error.NotAuthorized;
import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.granite.framework.core.annotations.AppComponent;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.auth.IAuthenticator;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfiguration;
import com.cloudeggtech.granite.framework.core.config.IApplicationConfigurationAware;
import com.cloudeggtech.granite.framework.core.event.IEventService;
import com.cloudeggtech.granite.framework.im.IResource;
import com.cloudeggtech.granite.framework.im.IResourcesService;
import com.cloudeggtech.granite.framework.im.ISubscriptionService;
import com.cloudeggtech.granite.framework.im.OfflineMessageEvent;
import com.cloudeggtech.granite.framework.im.Subscription;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;

@AppComponent("chat.message.deliverer")
public class ChatMessageDeliverer implements IApplicationConfigurationAware, IChatMessageDeliverer {
	
	@Dependency("authenticator")
	private IAuthenticator authenticator;
	
	@Dependency("resources.service")
	private IResourcesService resourcesService;
	
	@Dependency("subscription.service")
	private ISubscriptionService subscriptionService;
	
	private String domain;
	
	/* (non-Javadoc)
	 * @see com.cloudeggtech.granite.leps.im.IChatMessageDeliverer#isMessageDeliverable(com.cloudeggtech.granite.framework.processing.IProcessingContext, com.cloudeggtech.basalt.protocol.core.stanza.Stanza)
	 */
	@Override
	public boolean isMessageDeliverable(IProcessingContext context, Stanza stanza) {
		if ((stanza instanceof Message) && (((Message)stanza).getType()) == Message.Type.GROUPCHAT)
			return false;
		
		if (stanza.getTo() == null) {
			throw new ProtocolException(new BadRequest("A message should specify an intended recipient."));
		}
		
		JabberId from = stanza.getFrom() == null ? context.getJid() : stanza.getFrom();
		
		if (isToSelf(from, stanza.getTo())) {
			throw new ProtocolException(new BadRequest("Sending a message to yourself."));
		}
		
		if (!isToDomain(stanza.getTo())) {
			return false;
		}
		
		if (!authenticator.exists(stanza.getTo().getName())) {
			throw new ProtocolException(new NotAuthorized());
		}
		
		if (!isSubscribed(from, stanza.getTo())) {
			throw new ProtocolException(new Forbidden(String.format("%s and %s didn't subscribe each other.",
					from, stanza.getTo())));
		}
		
		return true;
	}

	private boolean isSubscribed(JabberId from, JabberId to) {
		String user = from.getName();
		String contact = to.getBareIdString();
		Subscription subscription = subscriptionService.get(user, contact);
		
		return subscription != null && Subscription.State.BOTH.equals(subscription.getState());
	}
	
	private boolean isToSelf(JabberId from, JabberId to) {
		return from.getBareIdString().equals(to.getBareIdString());
	}
	
	private boolean isToDomain(JabberId to) {
		return to.getDomain().equals(domain);
	}
	
	// Server Rules for Handling XML Stanzas(rfc3920 11)
	/* (non-Javadoc)
	 * @see com.cloudeggtech.granite.leps.im.IChatMessageDeliverer#deliver(com.cloudeggtech.granite.framework.processing.IProcessingContext, com.cloudeggtech.granite.framework.core.event.IEventService, com.cloudeggtech.basalt.protocol.im.stanza.Message)
	 */
	@Override
	public void deliver(IProcessingContext context, IEventService eventService, Message message) {
		JabberId to = message.getTo();
		if (to.getResource() != null) {
			IResource resoure = resourcesService.getResource(message.getTo());
			
			if (resoure != null && resoure.isAvailable()) {
				context.write(message);
				
				return;
			}
			
			to = to.getBareId();
		}
		
		IResource[] resources = resourcesService.getResources(to);
		if (resources.length == 0) {
			eventService.fire(new OfflineMessageEvent(context.getJid(), message.getTo(), message));
		} else {
			IResource[] chosen = chooseTargets(resources);
			if (chosen == null || chosen.length == 0) {
				eventService.fire(new OfflineMessageEvent(context.getJid(), message.getTo(), message));
			} else {
				for (IResource resource : chosen) {
					context.write(resource.getJid(), message);
				}
			}
		}
	}

	protected IResource[] chooseTargets(IResource[] resources) {
		List<IResource> lResources = new ArrayList<>();
		
		int priority = 0;
		
		for (IResource resource : resources) {
			if (!resource.isAvailable()) {
				continue;
			}
			
			int resourcePriority = 0;
			if (resource.getBroadcastPresence() != null && resource.getBroadcastPresence().getPriority() != null) {
				resourcePriority = resource.getBroadcastPresence().getPriority();
			}
			
			if(resourcePriority < priority) {
				continue;
			} else if (resourcePriority == priority) {
				lResources.add(resource);
			} else {
				lResources.clear();
				lResources.add(resource);
			}
		}
		
		return lResources.toArray(new IResource[lResources.size()]);
	}
	
	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		this.domain = appConfiguration.getDomainName();
	}
}
