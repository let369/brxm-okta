package org.example.sso;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.hippoecm.repository.security.DelegatingSecurityProvider;
import org.hippoecm.repository.security.RepositorySecurityProvider;
import org.hippoecm.repository.security.SecurityProvider;
import org.hippoecm.repository.security.user.DelegatingHippoUserManager;
import org.hippoecm.repository.security.user.HippoUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class SSODelegatingSecurityProvider extends DelegatingSecurityProvider {

    private static Logger log = LoggerFactory.getLogger(SSODelegatingSecurityProvider.class);

    private HippoUserManager userManager;

    public SSODelegatingSecurityProvider(final SecurityProvider delegatee) {
        super(delegatee);
    }

    public SSODelegatingSecurityProvider(){
        super(new RepositorySecurityProvider());
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by SSO.
     */
    @Override
    public UserManager getUserManager() throws RepositoryException {
        if (userManager == null) {
            userManager = new DelegatingHippoUserManager((HippoUserManager) super.getUserManager()) {
                @Override
                public boolean authenticate(SimpleCredentials creds) throws RepositoryException {
                    if (validateAuthentication(creds)) {
                        String userId = creds.getUserID();
                        if (!hasUser(userId)) {
                            return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        }

        return userManager;
    }

    /**
     * Returns a custom (delegating) HippoUserManager to authenticate a user by SSO.
     */
    @Override
    public UserManager getUserManager(Session session) throws RepositoryException {
        return new DelegatingHippoUserManager((HippoUserManager) super.getUserManager(session)) {
            @Override
            public boolean authenticate(SimpleCredentials creds) throws RepositoryException {
                if (validateAuthentication(creds)) {
                    String userId = creds.getUserID();
                    if (!hasUser(userId)) {
                        return false;
                    }
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    /**
     * Validates SAML SSO Assertion.
     * <p>
     * In this example, simply invokes SAML API (<code>AssertionHolder#getAssertion()</code>) to validate.
     * </P>
     *
     * @param creds
     * @return
     * @throws RepositoryException
     */
    protected boolean validateAuthentication(SimpleCredentials creds) throws RepositoryException {
        log.info("CustomDelegatingSecurityProvider validating credentials: {}", creds);

        SSOUserState userState = SSOLoginFilter.getCurrentSSOUserState();

        /*
         * If userState found in the current thread context, this authentication request came from
         * CMS application.
         * Otherwise, this authentication request came from SITE application (e.g, channel manager rest service).
         */

        if (userState != null) {

            // Asserting must have been done by the *AssertionValidationFilter* and the assertion thread local variable
            // must have been set by AssertionThreadLocalFilter already.
            // So, simply check if you have assertion object in the thread local.
            return StringUtils.isNotEmpty(userState.getCredentials().getUsername());

        } else {

            String samlId = (String) creds.getAttribute(SSOUserState.SAML_ID);

            if (StringUtils.isNotBlank(samlId)) {
                log.info("Authentication allowed to: {}", samlId);
                return true;
            }
        }

        return false;
    }
}
