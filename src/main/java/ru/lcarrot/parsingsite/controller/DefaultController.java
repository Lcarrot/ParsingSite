package ru.lcarrot.parsingsite.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @PreAuthorize("permitAll()")
    @GetMapping("/signIn")
    public String getPage() {
        return "page";
    }
}
