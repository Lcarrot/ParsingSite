package ru.lcarrot.parsingsite.controller.vk;

import com.squareup.okhttp.*;
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

    public OauthVkController(VkApiUtils vkApiUtils, VkService vkService) {
        this.vkApiUtils = vkApiUtils;
        this.vkService = vkService;
    }

    @GetMapping("/signIn")
    public String authorize(Optional<String> code, HttpSession httpSession) throws IOException {
        if (code.isPresent()) {
            URL url = vkApiUtils.getOauthURL(code.get());
            Call call = OkHttpUtils.getCallFromGetQuery(url);
            try (ResponseBody body = call.execute().body()) {
                User user = vkService.login(body);
                httpSession.setAttribute("user", user);
            }
            finally {
                call.cancel();
            }
        }
        return "redirect:/vk/groups";
    }
}
