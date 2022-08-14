package ru.lcarrot.parsingsite.controller.vk;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.service.UserService;
import ru.lcarrot.parsingsite.service.VkService;

@Controller
@RequestMapping("/vk/groups")
public class GroupsVkController {

    private final VkService vkService;
    private final UserService userService;

    public GroupsVkController(VkService vkService, UserService userService) {
        this.vkService = vkService;
        this.userService = userService;
    }

    @GetMapping
    public String groups(Model model) {
        User user = userService.getUser().get();
        List<Group> nodes = vkService.getGroups(user);
        model.addAttribute("nodes", nodes);
        return "groups";
    }

    @GetMapping("{group_id}")
    public String createOrChooseAlbum(@PathVariable("group_id") String group_id, Model model) {
        model.addAttribute("group_id", group_id);
        return "create_select_album";
    }
}
