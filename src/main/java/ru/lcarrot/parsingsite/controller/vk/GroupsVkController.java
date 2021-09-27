package ru.lcarrot.parsingsite.controller.vk;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.service.UserService;
import ru.lcarrot.parsingsite.service.VkService;
import ru.lcarrot.parsingsite.service.parse.ParseServiceManager;
import ru.lcarrot.parsingsite.util.OkHttpUtils;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Controller
@RequestMapping("/vk/groups")
public class GroupsVkController {

    private final VkApiUtils vkApiUtils;
    private final VkService vkService;
    private final UserService userService;
    private final OkHttpUtils okHttpUtils;

    public GroupsVkController(VkApiUtils vkApiUtils, VkService vkService, ParseServiceManager manager, ExecutorService executorService, UserService userService, OkHttpUtils okHttpUtils) {
        this.vkApiUtils = vkApiUtils;
        this.vkService = vkService;
        this.userService = userService;
        this.okHttpUtils = okHttpUtils;
    }

    @GetMapping
    public String groups(Model model, boolean reload) throws IOException {
        List<Group> nodes;
        User user = userService.getUser();
        if (user.getGroupList() != null && !reload) {
            nodes = user.getGroupList();
        } else {
            URL url = vkApiUtils.getGroupUrl(user);
            Call call = okHttpUtils.getCallFromGetQuery(url);
            try (ResponseBody getGroupsResponse = call.execute().body()) {
                nodes = vkService.getGroups(getGroupsResponse, user);
                user.setGroupList(nodes);
            } finally {
                call.cancel();
            }
        }
        model.addAttribute("nodes", nodes);
        return "groups";
    }

    // TODO: 06.09.2021 создать страницу для выбора
    @GetMapping("{group_id}")
    public String createOrChooseAlbum(@PathVariable("group_id") String group_id, Model model) {
        model.addAttribute("group_id", group_id);
        return "create_select_album";
    }
}
