package com.cloudeggtech.granite.xeps.ping;

import com.cloudeggtech.basalt.protocol.core.stanza.Iq;
import com.cloudeggtech.basalt.protocol.core.stanza.error.ServiceUnavailable;
import com.cloudeggtech.basalt.protocol.core.stanza.error.StanzaError;
import com.cloudeggtech.basalt.xeps.ping.Ping;
import com.cloudeggtech.granite.framework.core.config.IConfiguration;
import com.cloudeggtech.granite.framework.core.config.IConfigurationAware;
import com.cloudeggtech.granite.framework.processing.IProcessingContext;
import com.cloudeggtech.granite.framework.processing.IXepProcessor;

public class PingProcessor implements IXepProcessor<Iq, Ping>, IConfigurationAware {
	private static final String CONFIG_KEY_DISABLED = "disabled";
	private boolean disabled;

	@Override
	public void process(IProcessingContext context, Iq iq, Ping ping) {
		if (disabled) {
			ServiceUnavailable error = StanzaError.create(iq, ServiceUnavailable.class);
			context.write(error);
		} else {
			Iq pong = new Iq(Iq.Type.RESULT);
			pong.setId(iq.getId());
			pong.setObject(new Ping());
			
			context.write(pong);
		}
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		disabled = configuration.getBoolean(CONFIG_KEY_DISABLED, false);
	}
}
