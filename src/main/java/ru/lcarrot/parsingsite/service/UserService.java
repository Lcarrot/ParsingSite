package ru.lcarrot.parsingsite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.lcarrot.parsingsite.entity.User;

import javax.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    private HttpSession httpSession;

    public User getUser() {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = User.builder().id(authentication.getName())
                    .access_token((String) authentication.getCredentials()).build();
            httpSession.setAttribute("user", user);
        }
        return user;
    }
}
