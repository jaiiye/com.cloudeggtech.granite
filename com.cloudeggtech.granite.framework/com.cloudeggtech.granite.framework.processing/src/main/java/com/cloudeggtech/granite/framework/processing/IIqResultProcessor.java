package com.cloudeggtech.granite.framework.processing;

import com.cloudeggtech.basalt.protocol.core.stanza.Iq;

public interface IIqResultProcessor {
	boolean process(IProcessingContext context, Iq iq);
}
