package ru.lcarrot.parsingsite.controller;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.service.VkService;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class SimaLandController {

    private final VkApiUtils vkApiUtils;

    private final VkService vkService;

//    private final SimaLandItemToProductConverter productConverter;
//    private final UrlToDocumentConverter urlToDocumentConverter;
    private User user;
    OkHttpClient client = new OkHttpClient();

    public SimaLandController(VkApiUtils vkApiUtils, VkService vkService) {
        this.vkApiUtils = vkApiUtils;
        this.vkService = vkService;
    }

    @GetMapping("/simaland")
    public String getLinkAndDownload(String url) {
//        Document document = urlToDocumentConverter.convert(url);
//        Elements elements = document.getElementsByClass("catalog__item");
//        List<Product> productList = new ArrayList<>();
//        for (Element element: elements) {
//            productList.add(productConverter.convert(element));
//        }
        return "page";
    }

    @GetMapping("/getAccessToken")
    public String authorize(String code) throws IOException {
        URL url = vkApiUtils.getAuthURL(code);
        Request request = new Request.Builder().url(url).build();
        Response response = client.clone().newCall(request).execute();
        user = vkService.login(response);
        return "redirect:selectGroups";
    }

    @GetMapping("/selectGroups")
    public String selectGroups(Model model) throws IOException {
        URL httpUrl = vkApiUtils.getUrlForMethod("groups.get", getMap());
        Request request = new Request.Builder().url(httpUrl).build();
        Response response = client.clone().newCall(request).execute();
        List<Group> nodes = vkService.getGroups(response, user);
        model.addAttribute("nodes", nodes);
        return "groups";
    }

    private Map<String, String> getMap() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("access_token", user.getAccess_token());
        parameters.put("filter", "admin");
        parameters.put("bool", "1");
        return parameters;
    }
}
