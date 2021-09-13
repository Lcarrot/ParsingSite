package ru.lcarrot.parsingsite.controller.vk;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.ResponseBody;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.dto.AlbumDto;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.Product;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.service.UserService;
import ru.lcarrot.parsingsite.service.VkService;
import ru.lcarrot.parsingsite.service.parse.ParseService;
import ru.lcarrot.parsingsite.service.parse.ParseServiceManager;
import ru.lcarrot.parsingsite.util.OkHttpUtils;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/vk/group/{group_id}/album")
public class AlbumVkController {

    private final VkApiUtils vkApiUtils;
    private final VkService vkService;
    private final ParseServiceManager manager;
    private final UserService userService;
    private final OkHttpUtils okHttpUtils;

    public AlbumVkController(VkApiUtils vkApiUtils, VkService vkService, ParseServiceManager manager, UserService userService, OkHttpUtils okHttpUtils) {
        this.vkApiUtils = vkApiUtils;
        this.vkService = vkService;
        this.manager = manager;
        this.userService = userService;
        this.okHttpUtils = okHttpUtils;
    }

    @GetMapping("/albums")
    public String selectAlbum(@PathVariable("group_id") String group_id, boolean reload, Model model) throws IOException {
        List<Album> albums;
        User user = userService.getUser();
        Optional<Group> group = user.getGroupList().stream().filter(x -> x.getId().equals(group_id)).findFirst();
        if (group.isPresent() && group.get().getAlbums() != null && !reload) {
            albums = group.get().getAlbums();
        } else {
            URL url = vkApiUtils.getAlbumUrl(user, group_id);
            Call call = okHttpUtils.getCallFromGetQuery(url);
            try (ResponseBody getAlbumsResponse = call.execute().body()) {
                albums = vkService.getAlbums(getAlbumsResponse);
            }
        }
        model.addAttribute("albums", albums);
        return "select_album";
    }

    // TODO: 06.09.2021 страница для создания альбома
    @GetMapping("/create")
    public String getCreateAlbumPage(@PathVariable String group_id) {
        return "create_album";
    }

    // TODO: 06.09.2021 отправить запрос на создание альбома
    @PostMapping("/create")
    public String createAlbum(@PathVariable String group_id, AlbumDto albumDto) throws IOException {
        Album album = albumDto.to();
        album.setGroup_id(group_id);
        User user = userService.getUser();
        URL url = vkApiUtils.createAlbumUrl(user, album);
        Call call = okHttpUtils.getCallFromGetQuery(url);
        try (ResponseBody response = call.execute().body()) {
            album = vkService.getAlbum(response);
        }

        String redirect = "/vk/group/" + album.getGroup_id() + "/album/" + album.getId() + "/parse";
        return "redirect:" + redirect;
    }

    // TODO: 06.09.2021 сделать страницу списка парсеров
    @GetMapping(("/{album_id}/parse"))
    public String getSiteForParsing(@PathVariable String album_id, @PathVariable String group_id) {
        return "page";
    }

    @PostMapping("/{album_id}/parse")
    public String chooseSiteForParsing(@PathVariable String album_id, @PathVariable String group_id, String site, String url) throws IOException {
        User user = userService.getUser();
        ParseService parseService = manager.getParseServiceByName(site);
        Call call = okHttpUtils
                .getCallFromGetQuery(vkApiUtils
                        .getUploadServerUrl(user, group_id, album_id));
        String upload_url;
        try (ResponseBody body = call.execute().body()) {
            upload_url = vkApiUtils.getUploadUrl(body);
        } finally {
            call.cancel();
        }
        int count = parseService.getPageCount(parseService.getDocumentPageFromSite(url));
        Disposable disposable = Flowable.range(1, count)
                .map(pageNumber -> parseService.getDocumentPageByNumber(url, pageNumber))
                .map(parseService::getProducts).subscribe(list -> {
            for (Product product: list) {
                vkService.savePhotoInAlbum(SavePhoto.builder()
                        .album_id(album_id)
                        .user_access_token(user.getAccess_token())
                        .product(product)
                        .upload_url(upload_url)
                        .group_id(group_id)
                        .build());
            }
        });
        return "page";
    }
}
