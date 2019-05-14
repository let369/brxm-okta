package org.example.sso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

/**
 */
public class CmsUserDetailsService implements SAMLUserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CmsUserDetailsService.class);

    @Override
    public Object loadUserBySAML(final SAMLCredential samlCredential) throws UsernameNotFoundException {

        final String id = samlCredential.getNameID().getValue();
        log.debug("Credential name id = {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            log.debug("Authentication name = {}",  authentication.getName());
        }
        Map<String, List<String>> attributesMap = getAttributes(samlCredential);
//        final String uid = attributesMap.get("uid").get(0);
//        log.debug("uid =  {}", uid);
        final UserDetails userWithRoles = getUserWithRoles(samlCredential, id);
        log.info("userWithRoles {}", userWithRoles);
        return userWithRoles;
    }

    private UserDetails getUserWithRoles(SAMLCredential credential, String userId) {
        final List<SimpleGrantedAuthority> grantedRolesList = getGrantedAuthorities(credential);
        return new User(userId, userId, grantedRolesList);
    }

    private List<SimpleGrantedAuthority> getGrantedAuthorities(final SAMLCredential credential) {

        final List<SimpleGrantedAuthority> grantedRolesList = new ArrayList<SimpleGrantedAuthority>();
        grantedRolesList.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        grantedRolesList.add(new SimpleGrantedAuthority("ROLE_everybody"));
        grantedRolesList.add(new SimpleGrantedAuthority("ROLE_admin"));
        return grantedRolesList;
    }

    public static Map<String,List<String>> getAttributes(final SAMLCredential credential) {
        final List<Attribute> allAttributes = credential.getAttributes();

        Map<String, List<String>> attributeMap = new HashMap<>();
        for (Attribute attribute : allAttributes) {
            log.info("Parsing attribute: {}", attribute.getName());
            List<String> attributeValuesList = new LinkedList<>();

            final List<XMLObject> attributeValues = attribute.getAttributeValues();
            if (attributeValues == null) {
                continue;
            }
            for (XMLObject attributeValue : attributeValues) {
                final XSString xssString = (XSString) attributeValue;
                log.info("VALUE: {}", xssString.getValue());
                attributeValuesList.add(xssString.getValue());
            }
            attributeMap.put(attribute.getName(), attributeValuesList);
        }
        return attributeMap;
    }
}

