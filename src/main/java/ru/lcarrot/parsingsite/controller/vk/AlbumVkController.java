package ru.lcarrot.parsingsite.controller.vk;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.dto.AlbumDto;
import ru.lcarrot.parsingsite.dto.ParseInfoDto;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.entity.*;
import ru.lcarrot.parsingsite.service.UserService;
import ru.lcarrot.parsingsite.service.VkService;
import ru.lcarrot.parsingsite.service.parse.ParseService;
import ru.lcarrot.parsingsite.service.parse.ParseServiceManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ru.lcarrot.parsingsite.util.HtmParseUtils.getDocumentPageFromSite;

@Controller
@RequestMapping("/vk/group/{group_id}/album")
public class AlbumVkController {

    private final VkService vkService;
    private final ParseServiceManager manager;
    private final UserService userService;

    public AlbumVkController(VkService vkService, ParseServiceManager manager, UserService userService) {
        this.vkService = vkService;
        this.manager = manager;
        this.userService = userService;
    }

    @GetMapping("/albums")
    public String selectAlbum(@PathVariable("group_id") String group_id, boolean reload, Model model) throws IOException {
        List<Album> albums;
        User user = userService.getUser();
        Optional<Group> group = user.getGroupList().stream().filter(x -> x.getId().equals(group_id)).findFirst();
        if (group.isPresent() && group.get().getAlbums() != null && !reload) {
            albums = group.get().getAlbums();
        } else {
            albums = vkService.getAlbums(user, group_id);
        }
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
        User user = userService.getUser();
        album = vkService.createAlbum(user, album);
        String redirect = "/vk/group/" + album.getGroup_id() + "/album/" + album.getId() + "/parse";
        return "redirect:" + redirect;
    }

    // TODO: 06.09.2021 сделать страницу списка парсеров
    @GetMapping(("/{album_id}/parse"))
    public String getSiteForParsing(@PathVariable String album_id, @PathVariable String group_id, Model model) {
        model.addAttribute("services", manager.getAllServices());
        User user = userService.getUser();
        user.getTasks().removeIf(x -> x.getCompletableFuture().isDone());
        List<ParseInfoDto> parseInfoDtoList = user.getTasks().stream().map(ParseInfoDto::to).collect(Collectors.toList());
        model.addAttribute("tasks", parseInfoDtoList);
        return "page";
    }

    @PostMapping("/{album_id}/parse")
    public String chooseSiteForParsing(@PathVariable String album_id, @PathVariable String group_id, String site, String url) throws IOException {
        User user = userService.getUser();
        ParseService parseService = manager.getParseServiceByName(site);
        String upload_url = vkService.getUploadUrl(user, group_id, album_id);
        int count = parseService.getPageCount(getDocumentPageFromSite(url));
        AtomicInteger pageParsedCount = new AtomicInteger(0);
        Path folder = Paths.get(group_id, album_id);
        Files.createDirectories(folder);
        CompletableFuture<?> future = CompletableFuture.runAsync(() -> {
            Disposable disposable = Flowable.range(1, count)
                    .map(pageNumber -> {
                        pageParsedCount.set(pageNumber);
                        return parseService.getDocumentPageByNumber(url, pageNumber);
                    })
                    .map(parseService::getProducts).subscribeOn(Schedulers.computation()).subscribe(list -> {
                        for (Product product : list) {
                            vkService.savePhotoInAlbum(SavePhoto.builder()
                                    .album_id(album_id)
                                    .user_access_token(user.getAccess_token())
                                    .product(product)
                                    .upload_url(upload_url)
                                    .group_id(group_id)
                                    .build());
                        }
                    });
        });
        ParseInfo parseInfo = ParseInfo.builder()
                .completableFuture(future)
                .album_id(album_id)
                .url(url)
                .count(pageParsedCount)
                .allPagesCount(count)
                .build();
        user.getTasks().add(parseInfo);
        return "redirect:" + "/vk/group/" + group_id + "/album/" + album_id + "/parse";
    }
}
