package ru.lcarrot.parsingsite.controller.vk;

import com.squareup.okhttp.Response;
import org.jsoup.nodes.Document;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.lcarrot.parsingsite.dto.AlbumDto;
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
import rx.Observable;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

@Controller
@RequestMapping("/vk/group/{group_id}/album")
public class AlbumVkController {

    private final VkApiUtils vkApiUtils;
    private final VkService vkService;
    private final ParseServiceManager manager;
    private final ExecutorService executorService;
    private final UserService userService;
    private final OkHttpUtils okHttpUtils;

    public AlbumVkController(VkApiUtils vkApiUtils, VkService vkService, ParseServiceManager manager, ExecutorService executorService, UserService userService, OkHttpUtils okHttpUtils) {
        this.vkApiUtils = vkApiUtils;
        this.vkService = vkService;
        this.manager = manager;
        this.executorService = executorService;
        this.userService = userService;
        this.okHttpUtils = okHttpUtils;
    }

    private final String ALBUMS_GET_METHOD_NAME = "photos.getAlbums";
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/albums")
    public String selectAlbum(@PathVariable("group_id") String group_id, boolean reload, Model model, Principal principal) throws IOException {
        List<Album> albums;
        User user = userService.getUser(principal.getName());
        Optional<Group> group = user.getGroupList().stream().filter(x -> x.getId().equals(group_id)).findFirst();
        if (group.isPresent() && group.get().getAlbums() != null && !reload) {
            albums = group.get().getAlbums();
        }
        else {
            URL url = vkApiUtils.getUrlForMethod(ALBUMS_GET_METHOD_NAME, vkApiUtils.selectAlbumMap(user, group_id));
            Response getAlbumsResponse = okHttpUtils.getResponseFromGetQuery(url);
            albums = vkService.getAlbums(getAlbumsResponse);
        }
        model.addAttribute("albums", albums);
        return "select_album";
    }

    // TODO: 06.09.2021 страница для создания альбома
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String getCreateAlbumPage(@PathVariable String group_id) {
        return "create_album";
    }

    // TODO: 06.09.2021 отправить запрос на создание альбома
    private final String ALBUM_CREATE_METHOD_NAME = "photos.createAlbum";
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createAlbum(@PathVariable String group_id, AlbumDto albumDto, Principal principal) throws IOException {
        Album album = albumDto.to();
        album.setGroup_id(group_id);
        User user = userService.getUser(principal.getName());
        URL url = vkApiUtils.getUrlForMethod(ALBUM_CREATE_METHOD_NAME, vkApiUtils.createAlbumMap(user, album));
        Response response = okHttpUtils.getResponseFromGetQuery(url);
        album = vkService.getAlbum(response);
        return "redirect:/group/" + album.getGroup_id() + "/album/" + album.getId() +"/parse";
    }

    // TODO: 06.09.2021 сделать страницу списка парсеров
    @PreAuthorize("isAuthenticated()")
    @GetMapping(("/{album_id}/parse"))
    public String getSiteForParsing(@PathVariable String album_id, @PathVariable String group_id) {
        return "page";
    }

    private final String GET_UPLOAD_SERVER_METHOD_NAME = "photos.getUploadServer";
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{album_id}/parse")
    public String chooseSiteForParsing(@PathVariable String album_id, @PathVariable String group_id, String site, String url, Principal principal) throws IOException {
        User user = userService.getUser(principal.getName());
        ParseService parseService = manager.getParseServiceByName(site);
        Document document = parseService.getDocumentPageFromSite(url);
        List<Product> productList = new CopyOnWriteArrayList<>(parseService.getProducts(document));
        for (int i = 2; i < parseService.getPageCount(document); i++) {
            int finalI = i;
            executorService.execute(() -> {
                Document document1 = parseService.getNextPage(finalI);
                productList.addAll(parseService.getProducts(document1));
            });
        }
        String upload_url = vkService.getUploadUrl(okHttpUtils.getResponseFromGetQuery(
                vkApiUtils.getUrlForMethod(GET_UPLOAD_SERVER_METHOD_NAME,
                        vkApiUtils.getUploadUrlMap(user, group_id, album_id))));
        Observable.from(productList)
                .subscribe(product -> {
                    try {
                        vkService.savePhotoInAlbum(product,upload_url, user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return "page";
    }
}
