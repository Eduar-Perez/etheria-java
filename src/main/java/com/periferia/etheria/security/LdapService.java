package com.periferia.etheria.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import com.periferia.etheria.constants.Constants;
import com.periferia.etheria.exception.UserException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LdapService {

	public Map<String, String> authenticate(String email, String password) {
		log.info(Constants.LOGIN_SERVICE, Thread.currentThread().getStackTrace()[1].getMethodName());

		String identifyingAttribute = "sAMAccountName";
		String[] attributesToReturn = {"sn", "mail", "givenName", "sn", "employeeID"};

		Properties environment = new Properties();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, "");
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");

		try {
			DirContext dirContext = new InitialDirContext(environment);

			SearchControls searchControl = new SearchControls();
			searchControl.setReturningAttributes(new String [0]);
			searchControl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String searchFilter	 = "(" + identifyingAttribute + "=" + email + ")";  
			NamingEnumeration<SearchResult> results = dirContext.search("", searchFilter, searchControl);

			if (!results.hasMore()) {
				dirContext.close();
				throw new UserException(null, 0, null);
			}

			SearchResult result = results.next();
			String userDN = result.getNameInNamespace();

			Properties authEnv = new Properties();
			authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			authEnv.put(Context.PROVIDER_URL, "");
			authEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			authEnv.put(Context.SECURITY_PRINCIPAL, userDN);
			authEnv.put(Context.SECURITY_CREDENTIALS, password);

			DirContext authContext = new InitialDirContext(authEnv);
			Attributes attrs = authContext.getAttributes(userDN, attributesToReturn);
			Map<String, String> userAttributes = new HashMap<>();
			for(String attrName : attributesToReturn) {
				Attribute attr = attrs.get(attrName);
				if(attr != null) {
					userAttributes.put(attrName, attr.get().toString());
				}
			}
			dirContext.close();
			authContext.close();
			return userAttributes;

		} catch (AuthenticationException e) {
			throw new UserException("Credenciales inválidas: " + e.getMessage(), 401, e.getMessage());
		} catch (NameNotFoundException e) {
			throw new UserException("Usuario no encontrado en LDAP: " + e.getMessage() , 404, e.getMessage());
		} catch (CommunicationException e) {
			throw new UserException("No se pudo conectar al servidor LDAP: " + e.getMessage(), 503, e.getMessage());
		} catch (NamingException e) {
			throw new UserException("Error general al autenticar con LDAP: " + e.getMessage(), 500, e.getMessage());
		} catch (Exception e) {
			throw new UserException("Error inesperado al autenticar usuario: " + e.getMessage(), 500, e.getMessage());
		}
	}

}
