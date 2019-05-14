package org.example.sso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.SimpleCredentials;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hippoecm.frontend.model.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 */
public class SSOLoginFilter implements Filter {


    private static Logger log = LoggerFactory.getLogger(SSOLoginFilter.class);

    private static final String SSO_USER_STATE = SSOUserState.class.getName();

    private static ThreadLocal<SSOUserState> tlCurrentSSOUserState = new ThreadLocal<SSOUserState>();

    private String[] prefixExclusions;
    private String[] suffixExclusions;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        prefixExclusions = splitParamValue(filterConfig.getInitParameter("prefixExclusions"), ",");
        suffixExclusions = splitParamValue(filterConfig.getInitParameter("suffixExclusions"), ",");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        if (request instanceof HttpServletRequest) {

            HttpServletRequest req = (HttpServletRequest) request;

            // If the request path is for static resources such as images, css, etc., don't continue.
            if (isSkippableRequest(req)) {
                chain.doFilter(request, response);
                return;
            }

            // Check if the user already has a SSO user state stored in HttpSession before.
            HttpSession session = req.getSession();

            SSOUserState userState = (SSOUserState) session.getAttribute(SSO_USER_STATE);

            if (userState == null || !userState.getSessionId().equals(session.getId())) {

                final SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
                if (securityContext != null) {
                    final Authentication authentication = securityContext.getAuthentication();
                    User user = (User)authentication.getDetails();

                    // If the stored SSO user state is null or has a different JSESSIONID, then
                    // recreate a new SSO user state only when the user was already authenticated.
                    final String samlID = user.getUsername();

                    // Only when the user was already authenticated and so had a CAS NetID..
                    if (samlID != null) {
                        log.info("SamlID: {}", samlID);

                        // Enter any dummy string which must not be an empty string.
                        SimpleCredentials creds = new SimpleCredentials(samlID, "DUMMY".toCharArray());
                        creds.setAttribute(SSOUserState.SAML_ID, samlID);
                        userState = new SSOUserState(new UserCredentials(creds), session.getId());
                        session.setAttribute(SSO_USER_STATE, userState);
                    }
                }
            }
            // If the user has a valid SSO user state, then
            // set a JCR Credentials as request attribute (named by FQCN of UserCredentials class).
            // Then the CMS application will use the JCR credentials passed through this request attribute.
            if (userState != null && userState.getSessionId().equals(session.getId())) {
                req.setAttribute(UserCredentials.class.getName(), userState.getCredentials());
            }

            try {
                tlCurrentSSOUserState.set(userState);
                chain.doFilter(request, response);
            } finally {
                tlCurrentSSOUserState.remove();
            }
        }else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Get current <code>SSOUserState</code> instance from the current thread local context.
     * @return
     */
    static SSOUserState getCurrentSSOUserState() {
        return tlCurrentSSOUserState.get();
    }

    /**
     * Determine if the reuqest is for static resources.
     * @param request
     * @return
     */
    private boolean isSkippableRequest(final HttpServletRequest request) {
        String pathInfo = request.getRequestURI();

        if (pathInfo != null) {
            pathInfo = pathInfo.substring(request.getContextPath().length());
        }

        if (prefixExclusions != null) {
            for (String excludePrefix : prefixExclusions) {
                if (pathInfo.startsWith(excludePrefix)) {
                    return true;
                }
            }
        }

        if (suffixExclusions != null) {
            for (String excludeSuffix : suffixExclusions) {
                if (pathInfo.endsWith(excludeSuffix)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Split parameter string value into an array.
     *
     * @param param
     * @param delimiter
     * @return
     */
    private static String[] splitParamValue(String param, String delimiter) {
        if (param == null) {
            return null;
        }

        String[] tokens = param.split(delimiter);
        List<String> valuesList = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            String value = tokens[i].trim();
            if (!value.isEmpty()) {
                valuesList.add(value);
            }
        }

        if (!valuesList.isEmpty()) {
            return valuesList.toArray(new String[valuesList.size()]);
        }

        return null;
    }
}
