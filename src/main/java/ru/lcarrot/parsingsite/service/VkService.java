package ru.lcarrot.parsingsite.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.dto.UploadToServer;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.security.authentication.TokenAuthentication;
import ru.lcarrot.parsingsite.util.JsonUtils;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.lcarrot.parsingsite.util.OkHttpUtils.getCallFromGetQuery;
import static ru.lcarrot.parsingsite.util.OkHttpUtils.getResponseFromPostQuery;

@Component
public class VkService {

    private final VkApiUtils vkApiUtils;
    private final ConversionService conversionService;
    private final JsonUtils jsonUtils;

    public VkService(VkApiUtils vkApiUtils, ConversionService conversionService, JsonUtils jsonUtils) {
        this.vkApiUtils = vkApiUtils;
        this.conversionService = conversionService;
        this.jsonUtils = jsonUtils;
    }

    public User login(ResponseBody response) throws IOException {
        User user = conversionService.convert(jsonUtils.get(response), User.class);
        SecurityContextHolder.getContext().setAuthentication(new TokenAuthentication(user, true));
        return user;
    }

    public List<Group> getGroups(ResponseBody response, User user) throws IOException {
        JsonNode jsonNode = jsonUtils.get(response, "response");
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
        URL url = vkApiUtils.setValueForGroupIdMap(groupId, user);
        Request request = new Request.Builder().url(url).build();
        Group group;
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            JsonNode node = jsonUtils.get(response.body(), "response").elements().next();
            group = conversionService.convert(node, Group.class);
        } catch (IOException e) {
            throw new RuntimeException("can't parse a group");
        }
        return group;
    }

    public Album getAlbum(ResponseBody response) throws IOException {
        JsonNode node = jsonUtils.get(response, "response");
        return conversionService.convert(node, Album.class);
    }

    public List<Album> getAlbums(ResponseBody response) throws IOException {
        JsonNode node = jsonUtils.get(response, "response");
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

    public void savePhotoInAlbum(final SavePhoto savePhoto) throws IOException {
        Call call1 = getResponseFromPostQuery(new URL(savePhoto.getUpload_url()),
                        vkApiUtils.requestBodyForUploadServer(savePhoto.getProduct().getImage(),savePhoto.getAlbum_id(), savePhoto.getGroup_id()));
        try (ResponseBody responseBody = call1.execute().body()){
            JsonNode node = jsonUtils.get(responseBody);
            UploadToServer upload = conversionService.convert(node, UploadToServer.class);
            assert upload != null;
            upload.setDescription(savePhoto.getProduct().getDescription());
            upload.setAccess_token(savePhoto.getUser_access_token());
            Call call = getCallFromGetQuery(vkApiUtils.getSavePhotoUrl(upload));
            try (ResponseBody body = call.execute().body()) {
                System.out.println(body.string());
                body.close();
                call.cancel();
                savePhoto.getProduct().getImage().delete();
            }
            finally {
                call.cancel();
            }
        }
        finally {
            call1.cancel();
        }
    }
}
