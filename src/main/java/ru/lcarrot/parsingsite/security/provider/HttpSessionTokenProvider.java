package ru.lcarrot.parsingsite.security.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.security.authentication.TokenAuthentication;

@Component
public class HttpSessionTokenProvider implements AuthenticationProvider {

    @Autowired
    @Qualifier("myUserDetailsService")
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");
        if (userDetails != null && userDetails.getUsername().equals(tokenAuthentication.getName())) {
            tokenAuthentication.setAuthenticated(true);
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return TokenAuthentication.class.equals(aClass);
    }
}
