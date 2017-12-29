package com.cloudeggtech.granite.lite.auth;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cloudeggtech.granite.framework.core.auth.Account;
import com.cloudeggtech.granite.framework.core.auth.IAuthenticator;

@Transactional
@Component
public class Authenticator implements IAuthenticator {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public Object getCredentials(Object principal) {
		AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
		
		Account account = mapper.selectByName((String)principal);
		return account.getPassword();
	}

	@Override
	public boolean exists(Object principal) {
		AccountMapper mapper = sqlSession.getMapper(AccountMapper.class);
		int count = mapper.selectCountByName((String)principal);
		
		return count != 0;
	}

}
