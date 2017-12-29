package com.cloudeggtech.granite.xeps.msgoffline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.basalt.protocol.core.stanza.Stanza;
import com.cloudeggtech.basalt.protocol.datetime.DateTime;
import com.cloudeggtech.basalt.protocol.im.stanza.Message;
import com.cloudeggtech.basalt.protocol.oxm.IOxmFactory;
import com.cloudeggtech.basalt.protocol.oxm.OxmService;
import com.cloudeggtech.basalt.protocol.oxm.convention.NamingConventionTranslatorFactory;
import com.cloudeggtech.basalt.protocol.oxm.translators.im.MessageTranslatorFactory;
import com.cloudeggtech.basalt.xeps.delay.Delay;
import com.cloudeggtech.granite.framework.core.annotations.Dependency;
import com.cloudeggtech.granite.framework.core.config.IConfiguration;
import com.cloudeggtech.granite.framework.core.config.IConfigurationAware;
import com.cloudeggtech.granite.framework.core.event.IEventContext;
import com.cloudeggtech.granite.framework.core.event.IEventListener;
import com.cloudeggtech.granite.framework.im.IOfflineMessageStore;
import com.cloudeggtech.granite.framework.im.OfflineMessageEvent;

public class OfflineMessageListener implements IEventListener<OfflineMessageEvent>,
			IConfigurationAware {
	private static final Logger logger = LoggerFactory.getLogger(OfflineMessageListener.class);
	
	private static final String CONFIGURATION_KEY_DISABLED = "disabled";
	
	private boolean disabled;
	
	@Dependency("offline.message.store")
	private IOfflineMessageStore offlineMessageStore;
	
	private IOxmFactory oxmFactory = OxmService.createMinimumOxmFactory();
	
	public OfflineMessageListener() {
		oxmFactory.register(
				Message.class,
				new MessageTranslatorFactory()
		);
		oxmFactory.register(
				Delay.class,
				new NamingConventionTranslatorFactory<>(
						Delay.class
				)
		);
	}
	
	@Override
	public void process(IEventContext context, OfflineMessageEvent event) {
		if (disabled)
			return;
		
		JabberId jid = event.getContact();
		Message message = event.getMessage();
		
		if (message.getId() == null) {
			message.setId(Stanza.generateId("om"));
		}
		
		message.setObject(new Delay(message.getFrom(), new DateTime()));
		
		if (message.getFrom() == null) {
			message.setFrom(event.getUser());
		}
		
		try {
			offlineMessageStore.save(jid, message.getId(), oxmFactory.translate(message));
		} catch (Exception e) {
			logger.error("Can't save offline message.", e);
		}
		
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		disabled = configuration.getBoolean(CONFIGURATION_KEY_DISABLED, false);
	}

}
