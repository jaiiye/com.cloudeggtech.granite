package com.cloudeggtech.granite.xeps.ibr;

import com.cloudeggtech.basalt.xeps.ibr.IqRegister;
import com.cloudeggtech.granite.framework.core.auth.Account;

public interface IRegistrationStrategy {
	IqRegister getRegistrationForm();
	Account convertToAccount(IqRegister iqRegister) throws MalformedRegistrationInfoException;
}
