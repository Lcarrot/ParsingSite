package ru.lcarrot.parsingsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    @GetMapping("/signIn")
    public String getPage() {
        return "sign_in";
    }
}
