package com.cloudeggtech.granite.framework.parsing;

public interface IPipePreprocessor {
	String beforeParsing(String message);
	Object afterParsing(Object object);
}
