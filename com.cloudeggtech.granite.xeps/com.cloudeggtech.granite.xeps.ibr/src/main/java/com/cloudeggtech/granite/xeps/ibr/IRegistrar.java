package com.cloudeggtech.granite.xeps.ibr;

import com.cloudeggtech.basalt.xeps.ibr.IqRegister;

public interface IRegistrar {
	IqRegister getRegistrationForm();
	void register(IqRegister iqRegister);
	void remove(String username);
}
