package ru.lcarrot.parsingsite.controller.vk;

import com.squareup.okhttp.Response;
import org.springframework.security.access.prepost.PreAuthorize;
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
import java.security.Principal;
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

    private final String GROUPS_GET_METHOD_NAME = "groups.get";
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public String groups(Model model, boolean reload, Principal principal) throws IOException {
        List<Group> nodes;
        User user = userService.getUser(principal.getName());
        if (user.getGroupList() != null && !reload) {
            nodes = user.getGroupList();
        }
        else {
            URL httpUrl = vkApiUtils.getUrlForMethod(GROUPS_GET_METHOD_NAME, vkApiUtils.setGroupMap(user));
            Response getGroupsResponse = okHttpUtils.getResponseFromGetQuery(httpUrl);
            nodes = vkService.getGroups(getGroupsResponse, user);
            user.setGroupList(nodes);
        }
        model.addAttribute("nodes", nodes);
        return "groups";
    }

    // TODO: 06.09.2021 создать страницу для выбора
    @PreAuthorize("isAuthenticated()")
    @GetMapping("{group_id}")
    public String createOrChooseAlbum(@PathVariable("group_id") String group_id, Model model) {
        model.addAttribute("group_id", group_id);
        return "create_select_album";
    }
}
