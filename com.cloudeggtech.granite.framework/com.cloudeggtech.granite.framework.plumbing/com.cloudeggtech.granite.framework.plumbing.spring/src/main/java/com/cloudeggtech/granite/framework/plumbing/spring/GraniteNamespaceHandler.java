package com.cloudeggtech.granite.framework.plumbing.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class GraniteNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("component", new ComponentBeanDefinitionParser());
	}

}
