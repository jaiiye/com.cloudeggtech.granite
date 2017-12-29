package com.cloudeggtech.granite.framework.processing;

import com.cloudeggtech.basalt.protocol.core.stanza.Stanza;

public interface IXepProcessor<K extends Stanza, V> {
	void process(IProcessingContext context, K stanza, V xep);
}
