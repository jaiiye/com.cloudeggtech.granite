package com.cloudeggtech.granite.framework.processing;

import com.cloudeggtech.basalt.protocol.core.JabberId;
import com.cloudeggtech.granite.framework.core.connection.IConnectionContext;

public interface IProcessingContext extends IConnectionContext {
	void write(JabberId target, Object message);
}
