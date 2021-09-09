package ru.lcarrot.parsingsite.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.*;
import ru.lcarrot.parsingsite.security.authentication.TokenAuthentication;
import ru.lcarrot.parsingsite.util.OkHttpUtils;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class VkService {

    private final ObjectMapper objectMapper;
    private final VkApiUtils vkApiUtils;
    private final OkHttpUtils okHttpUtils;
    private final ConversionService conversionService;

    public VkService(ObjectMapper objectMapper, VkApiUtils vkApiUtils, OkHttpUtils okHttpUtils, ConversionService conversionService) {
        this.objectMapper = objectMapper;
        this.vkApiUtils = vkApiUtils;
        this.okHttpUtils = okHttpUtils;
        this.conversionService = conversionService;
    }

    public User login(Response response) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(response.body().string());
        String id = jsonNode.get("user_id").asText();
        String access_token = jsonNode.get("access_token").asText();
        User user = User.builder().id(id).access_token(access_token).build();
        SecurityContextHolder.getContext().setAuthentication(new TokenAuthentication(user, true));
        return user;
    }

    public List<Group> getGroups(Response response, User user) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(response.body().string()).get("response");
        int count = jsonNode.get("count").asInt();
        List<Group> groupList;
        if (count > 100_000) {
            groupList = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(jsonNode.get("items").elements(), Spliterator.CONCURRENT),
                    true)
                    .map(x -> getGroupById(x.asText(), user))
                    .collect(Collectors.toList());
        }
        else {
            groupList = new ArrayList<>();
            for (Iterator<JsonNode> it = jsonNode.get("items").elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                groupList.add(getGroupById(node.asText(), user));
            }
        }
        return groupList;
    }

    private Group getGroupById(String groupId, User user) {
        URL url = vkApiUtils.getUrlForMethod("groups.getById", vkApiUtils.setValueForGroupIdMap(groupId, user));
        Request request = new Request.Builder().url(url).build();
        Group group;
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            JsonNode node = objectMapper.readTree(response.body().string()).get("response").elements().next();
            group = conversionService.convert(node, Group.class);
        } catch (IOException e) {
            throw new RuntimeException("can't parse a group");
        }
        return group;
    }

    public Album getAlbum(Response response) throws IOException {
        JsonNode node = objectMapper.readTree(response.body().string()).get("response");
        return conversionService.convert(node, Album.class);
    }

    public List<Album> getAlbums(Response response) throws IOException {
        JsonNode node = objectMapper.readTree(response.body().string()).get("response");
        int count = node.get("count").asInt();
        List<Album> albumList;
        if (count > 100_000) {
            albumList = StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(node.get("items").elements(), Spliterator.CONCURRENT),
                            true)
                    .map(o ->conversionService.convert(o, Album.class))
                    .collect(Collectors.toList());
        }
        else {
            albumList = new ArrayList<>();
            for (Iterator<JsonNode> it = node.get("items").elements(); it.hasNext(); ) {
                JsonNode next = it.next();
                albumList.add(conversionService.convert(next, Album.class));
            }
        }
        return albumList;
    }

    //todo: загружать фото на сервер и выгружать в альбом
    public void savePhotoInAlbum(Product product,String upload_url, User user) throws IOException {
        Response response = okHttpUtils
                .getResponseFromPostQuery(new URL(upload_url),
                        vkApiUtils.requestBodyForUploadServer(product.getImage()));
        JsonNode node = objectMapper.readTree(response.body().string());
        UploadToServer upload = conversionService.convert(node, UploadToServer.class);
        assert upload != null;
        upload.setAccess_token(user.getAccess_token());
        okHttpUtils.getResponseFromGetQuery(vkApiUtils
                .getUrlForMethod("photos.save",vkApiUtils.getSavePhotoMap(upload)));
    }

    public String getUploadUrl(Response response) throws IOException {
        return objectMapper.readTree(response.body().string()).get("response").get("upload_url").asText();
    }
}
