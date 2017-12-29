package com.cloudeggtech.granite.cluster.xeps.ibr;

import org.springframework.stereotype.Component;

import com.cloudeggtech.basalt.xeps.ibr.IqRegister;
import com.cloudeggtech.basalt.xeps.ibr.RegistrationField;
import com.cloudeggtech.basalt.xeps.ibr.RegistrationForm;
import com.cloudeggtech.granite.framework.core.auth.Account;
import com.cloudeggtech.granite.xeps.ibr.IRegistrationStrategy;

@Component
public class RegistrationStrategy implements IRegistrationStrategy {

	@Override
	public IqRegister getRegistrationForm() {
		RegistrationField username = new RegistrationField("username");
		RegistrationField password = new RegistrationField("password");
		RegistrationField email = new RegistrationField("email");
		
		RegistrationForm form = new RegistrationForm();
		form.getFields().add(username);
		form.getFields().add(password);
		form.getFields().add(email);
		
		IqRegister iqRegister = new IqRegister();
		iqRegister.setRegister(form);
		
		return iqRegister;
	}

	@Override
	public Account convertToAccount(IqRegister iqRegister) {
		RegistrationForm form = (RegistrationForm)iqRegister.getRegister();
		Account account = new Account();
		for (RegistrationField field : form.getFields()) {
			if ("username".equals(field.getName())) {
				account.setName(field.getValue());
			} else if ("password".equals(field.getName())) {
				account.setPassword(field.getValue());
			} else {
				// ignore
			}
		}
		
		return account;
	}

}
