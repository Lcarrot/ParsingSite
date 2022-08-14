package ru.lcarrot.parsingsite.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.security.authentication.TokenAuthentication;

import javax.servlet.http.HttpSession;

@Service
public class UserService {

  public Optional<User> getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return Optional.ofNullable(
        authentication != null ? (User) authentication.getPrincipal() : null);
  }

  public void login(User user) {
    if (user != null) {
      SecurityContextHolder.getContext().setAuthentication(new TokenAuthentication(user, true));
    }
  }
}
