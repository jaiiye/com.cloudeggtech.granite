package com.cloudeggtech.granite.framework.core.auth;

public interface IAuthenticator {
	Object getCredentials(Object principal) throws PrincipalNotFoundException;
	boolean exists(Object principal);
}
