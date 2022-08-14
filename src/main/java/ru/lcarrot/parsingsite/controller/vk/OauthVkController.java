package ru.lcarrot.parsingsite.controller.vk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.service.VkService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/vk")
public class OauthVkController {

    private final VkService vkService;

    public OauthVkController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping("/signIn")
    public String authorize(Optional<String> code) {
        code.ifPresent(vkService::login);
        return "redirect:/vk/groups";
    }
}
