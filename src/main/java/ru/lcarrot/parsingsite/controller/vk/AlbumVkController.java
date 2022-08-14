package ru.lcarrot.parsingsite.controller.vk;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.dto.AlbumDto;
import ru.lcarrot.parsingsite.dto.ParseInfoDto;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.ParseInfo;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.parser.ParserManager;
import ru.lcarrot.parsingsite.service.UserParseTaskService;
import ru.lcarrot.parsingsite.service.UserService;
import ru.lcarrot.parsingsite.service.VkService;

@Controller
@RequestMapping("/vk/group/{group_id}/album")
public class AlbumVkController {

  private final VkService vkService;
  private final UserParseTaskService userParseTaskService;
  private final ParserManager manager;
  private final UserService userService;

  public AlbumVkController(VkService vkService,
      UserParseTaskService userParseTaskService, ParserManager manager,
      UserService userService) {
    this.vkService = vkService;
    this.userParseTaskService = userParseTaskService;
    this.manager = manager;
    this.userService = userService;
  }

  @GetMapping("/albums")
  public String selectAlbum(@PathVariable("group_id") String group_id, boolean reload, Model model) {
    User user = userService.getUser().get();
    List<Album> albums = vkService.getAlbums(user, group_id, reload);
    model.addAttribute("albums", albums);
    return "select_album";
  }

  @GetMapping("/create")
  public String getCreateAlbumPage(@PathVariable String group_id) {
    return "create_album";
  }

  @PostMapping("/create")
  public String createAlbum(@PathVariable String group_id, AlbumDto albumDto) {
    Album album = albumDto.to();
    album.setGroup_id(group_id);
    User user = userService.getUser().get();
    album = vkService.createAlbum(user, album);
    String redirect = "/vk/group/" + album.getGroup_id() + "/album/" + album.getId() + "/parse";
    return "redirect:" + redirect;
  }

  @GetMapping(("/{album_id}/parse"))
  public String getSiteForParsing(@PathVariable String album_id, @PathVariable String group_id,
      Model model) {
    model.addAttribute("services", manager.getAllServices());
    User user = userService.getUser().get();
    List<ParseInfoDto> parseInfoDtoList = userParseTaskService.getCurrentUserTasks(user);
    model.addAttribute("tasks", parseInfoDtoList);
    return "page";
  }

  @PostMapping("/{album_id}/parse")
  public String chooseSiteForParsing(@PathVariable String album_id, @PathVariable String group_id,
      String site, String url) {
    User user = userService.getUser().get();
    ParseInfo parseInfo = vkService.getParseTask(site, user, group_id, album_id, url);
    userParseTaskService.addTasksToUser(user.getId(), parseInfo);
    return "redirect:" + "/vk/group/" + group_id + "/album/" + album_id + "/parse";
  }
}
