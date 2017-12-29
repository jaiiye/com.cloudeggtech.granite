package com.cloudeggtech.granite.lite.auth;

import com.cloudeggtech.granite.framework.core.auth.Account;

public interface AccountMapper {
	void insert(Account account);
	void delete(String name);
	Account selectByName(String name);
	int selectCountByName(String name);
}
