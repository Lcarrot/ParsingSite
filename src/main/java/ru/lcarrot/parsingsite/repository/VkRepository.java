package ru.lcarrot.parsingsite.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.*;
import lombok.SneakyThrows;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.dto.UploadToServer;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.util.JsonUtils;
import ru.lcarrot.parsingsite.util.OkHttpUtils;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.lcarrot.parsingsite.util.OkHttpUtils.getCallFromGetQuery;
import static ru.lcarrot.parsingsite.util.OkHttpUtils.getResponseFromPostQuery;

@Component
public class VkRepository {

    private final VkApiUtils vkApiUtils;
    private final JsonUtils jsonUtils;
    private final ConversionService conversionService;

    public VkRepository(VkApiUtils vkApiUtils, JsonUtils jsonUtils, ConversionService conversionService) {
        this.vkApiUtils = vkApiUtils;
        this.jsonUtils = jsonUtils;
        this.conversionService = conversionService;
    }

    public User login(String code) throws IOException {
        URL url = vkApiUtils.getOauthURL(code);
        Call call = OkHttpUtils.getCallFromGetQuery(url);
        try (ResponseBody body = call.execute().body()) {
            return conversionService.convert(jsonUtils.get(body), User.class);
        }
        finally {
            call.cancel();
        }
    }

    @SneakyThrows
    public List<Group> getGroupList(User user) {
        URL url = vkApiUtils.getGroupUrl(user);
        Call call = OkHttpUtils.getCallFromGetQuery(url);
        try (ResponseBody getGroupsResponse = call.execute().body()) {
            JsonNode jsonNode = jsonUtils.get(getGroupsResponse, "response");
            int count = jsonNode.get("count").asInt();
            if (count > 100_000) {
                return StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(jsonNode.get("items").elements(), Spliterator.CONCURRENT),
                                true)
                        .map(x -> getGroupById(x.asText(), user))
                        .collect(Collectors.toList());
            } else {
                List<Group> groupList = new ArrayList<>();
                for (Iterator<JsonNode> it = jsonNode.get("items").elements(); it.hasNext(); ) {
                    JsonNode node = it.next();
                    groupList.add(getGroupById(node.asText(), user));
                }
                return groupList;
            }
        }
    }

    public Group getGroupById(String groupId, User user) {
        URL url = vkApiUtils.setValueForGroupIdMap(groupId, user);
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            JsonNode node = jsonUtils.get(response.body(), "response").elements().next();
            return conversionService.convert(node, Group.class);
        } catch (IOException e) {
            throw new RuntimeException("can't parse a group");
        }
    }

    @SneakyThrows
    public Album createAlbum(User user, Album album) {
        URL url = vkApiUtils.createAlbumUrl(user, album);
        Call call = OkHttpUtils.getCallFromGetQuery(url);
        try (ResponseBody response = call.execute().body()) {
            JsonNode node = jsonUtils.get(response, "response");
            return conversionService.convert(node, Album.class);
        }
    }

    public List<Album> getAlbums(User user, String group_id) throws IOException {
        URL url = vkApiUtils.getAlbumUrl(user, group_id);
        Call call = OkHttpUtils.getCallFromGetQuery(url);
        try (ResponseBody getAlbumsResponse = call.execute().body()) {
            JsonNode node = jsonUtils.get(getAlbumsResponse, "response");
            int count = node.get("count").asInt();
            if (count > 100_000) {
                return StreamSupport
                        .stream(Spliterators.spliteratorUnknownSize(node.get("items").elements(), Spliterator.CONCURRENT),
                                true)
                        .map(o -> conversionService.convert(o, Album.class))
                        .collect(Collectors.toList());
            } else {
                List<Album> albumList = new ArrayList<>();
                for (Iterator<JsonNode> it = node.get("items").elements(); it.hasNext(); ) {
                    JsonNode next = it.next();
                    albumList.add(conversionService.convert(next, Album.class));
                }
                return albumList;
            }
        }
    }

    @SneakyThrows
    public void savePhoto(SavePhoto savePhoto, File image) {
        Call call1 = getResponseFromPostQuery(new URL(savePhoto.getUpload_url()),
                vkApiUtils.requestBodyForUploadServer(image,savePhoto.getAlbum_id(), savePhoto.getGroup_id()));
        try (ResponseBody responseBody = call1.execute().body()){
            JsonNode node = jsonUtils.get(responseBody);
            String folderName = savePhoto.getUser_access_token() + ThreadLocalRandom.current();
            UploadToServer upload = conversionService.convert(node, UploadToServer.class);
            assert upload != null;
            upload.setFolderName(folderName);
            upload.setDescription(savePhoto.getProduct().getDescription());
            upload.setAccess_token(savePhoto.getUser_access_token());
            Call call = getCallFromGetQuery(vkApiUtils.getSavePhotoUrl(upload));
            try (ResponseBody body = call.execute().body()) {
                System.out.println(body.string());
                body.close();
                call.cancel();
            }
            finally {
                call.cancel();
            }
        }
        finally {
            call1.cancel();
        }
    }

    @SneakyThrows
    public String getUploadUrl(User user, String group_id, String album_id) {
        Call call = OkHttpUtils
                .getCallFromGetQuery(vkApiUtils
                        .getUploadServerUrl(user, group_id, album_id));
        try (ResponseBody body = call.execute().body()) {
            return vkApiUtils.getUploadUrl(body);
        } finally {
            call.cancel();
        }
    }
}
