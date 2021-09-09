package ru.lcarrot.parsingsite.security.details;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lcarrot.parsingsite.entity.User;

import javax.servlet.http.HttpSession;

@Service("myUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private HttpSession httpSession;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new UserDetailsImpl((User) httpSession.getAttribute("user"));
    }
}
