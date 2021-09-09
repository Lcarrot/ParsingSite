package ru.lcarrot.parsingsite.security.authentication;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.security.details.UserDetailsImpl;

import java.util.Collection;
import java.util.Collections;


@AllArgsConstructor
public class TokenAuthentication implements Authentication {

    private final User user;
    private boolean isAuthenticated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("user");
        return Collections.singleton(authority);
    }

    @Override
    public Object getCredentials() {
        return user.getAccess_token();
    }

    @Override
    public Object getDetails() {
        return new UserDetailsImpl(user);
    }

    @Override
    public Object getPrincipal() {
        return user.getId();
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        isAuthenticated = b;
    }

    @Override
    public String getName() {
        return user.getId();
    }
}
