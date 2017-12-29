package com.cloudeggtech.granite.framework.routing;

import com.cloudeggtech.granite.framework.core.integration.IMessage;

public interface IPipePostprocessor {
	IMessage beforeRouting(IMessage message);
}
