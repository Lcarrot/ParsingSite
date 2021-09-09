package ru.lcarrot.parsingsite.controller.vk;

import com.squareup.okhttp.Response;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.service.VkService;
import ru.lcarrot.parsingsite.util.OkHttpUtils;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Controller
@RequestMapping("/vk")
public class OauthVkController {

    private final VkApiUtils vkApiUtils;
    private final VkService vkService;
    private final OkHttpUtils okHttpUtils;

    public OauthVkController(VkApiUtils vkApiUtils, VkService vkService, OkHttpUtils okHttpUtils) {
        this.vkApiUtils = vkApiUtils;
        this.vkService = vkService;
        this.okHttpUtils = okHttpUtils;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/signIn")
    public String authorize(Optional<String> code, HttpSession httpSession) throws IOException {
        if (code.isPresent()) {
            URL url = vkApiUtils.getOauthURL(code.get());
            Response oauthResponse = okHttpUtils.getResponseFromGetQuery(url);
            User user = vkService.login(oauthResponse);
            httpSession.setAttribute("user", user);
        }
        return "redirect:/vk/groups";
    }
}
