package ru.lcarrot.parsingsite.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.dto.UploadToServer;
import ru.lcarrot.parsingsite.entity.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Component
public class VkApiUtils {

    @Value("${client_secret}")
    private String client_secret;

    @Value("${client_id}")
    private String client_id;

    @Value("${domain}")
    private String domain;

    private final String vk_version = "5.131";

    @Autowired
    private ObjectMapper objectMapper;

    public URL getOauthURL(final String code) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("oauth.vk.com")
                .addPathSegment("access_token")
                .addQueryParameter("client_id", client_id)
                .addQueryParameter("client_secret", client_secret)
                .addQueryParameter("redirect_uri", domain + "/vk/signIn")
                .addQueryParameter("code", code).build().url();
    }

    private HttpUrl.Builder getBaseUrlMethod(final String methodName) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("api.vk.com")
                .addPathSegment("method")
                .addPathSegment(methodName)
                .addQueryParameter("v", vk_version);
    }

    public URL getGroupUrl(final User user) {
        return getBaseUrlMethod("groups.get").
                addQueryParameter("user_id", user.getId()).
                addQueryParameter("access_token", user.getAccess_token()).
                addQueryParameter("filter", "admin").
                addQueryParameter("bool", "1").build().url();
    }

    public URL createAlbumUrl(final User user, final Album album) {
        String methodName = "photos.createAlbum";
        return getBaseUrlMethod(methodName).
                addQueryParameter("access_token", user.getAccess_token()).
                addQueryParameter("title", album.getName()).
                addQueryParameter("group_id", album.getGroup_id()).
                addQueryParameter("description", album.getDescription()).
                addQueryParameter("upload_by_admins_only", "1").build().url();
    }

    public URL getAlbumUrl(final User user, final String group_id) {
        String methodName = "photos.getAlbums";
        return getBaseUrlMethod(methodName)
                .addQueryParameter("access_token", user.getAccess_token())
                .addQueryParameter("owner_id", "-" + group_id)
                .addQueryParameter("need_covers", "1").build().url();
    }

    public URL setValueForGroupIdMap(final String group_id, final User user) {
        return getBaseUrlMethod("groups.getById").
                addQueryParameter("access_token", user.getAccess_token()).
                addQueryParameter("group_id", group_id).build().url();
    }

    public URL getUploadServerUrl(final User user, final String group_id, final String album_id) {
        String methodName = "photos.getUploadServer";
        return getBaseUrlMethod(methodName).
                addQueryParameter("access_token", user.getAccess_token()).
                addQueryParameter("album_id", album_id).
                addQueryParameter("group_id", group_id).build().url();
    }

    public RequestBody requestBodyForUploadServer(File file, String album_id, String group_id) {
        return new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("album_id", album_id)
                .addFormDataPart("group_id", group_id)
                .build();
    }

    public URL getSavePhotoUrl(UploadToServer upload) {
        String methodName = "photos.save";
        return getBaseUrlMethod(methodName).
                addQueryParameter("access_token", upload.getAccess_token()).
                addQueryParameter("album_id", upload.getAlbum_id()).
                addQueryParameter("group_id", upload.getGroup_id()).
                addQueryParameter("server", upload.getServer()).
                addQueryParameter("photos_list", upload.getPhotos_list()).
                addQueryParameter("hash", upload.getHash()).
                addQueryParameter("caption", upload.getDescription()).build().url();
    }

    public String getUploadUrl(ResponseBody response) throws IOException {
        return objectMapper.readTree(response.string()).get("response").get("upload_url").asText();
    }
}
