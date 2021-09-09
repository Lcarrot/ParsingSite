package ru.lcarrot.parsingsite.util;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.UploadToServer;
import ru.lcarrot.parsingsite.entity.User;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
public class VkApiUtils {

    @Value("${client_secret}")
    private String client_secret;

    @Value("${client_id}")
    private String client_id;

    @Value("${domain}")
    private String domain;

    private final String vk_version = "5.131";

    public URL getUrlForMethod(final String method, Map<String, String> parameters) {
        HttpUrl.Builder httpUrl = getBaseUrlMethod().addPathSegment(method);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            httpUrl.addQueryParameter(parameter.getKey(), parameter.getValue());
        }
        return httpUrl.addQueryParameter("v", vk_version).build().url();
    }

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

    private HttpUrl.Builder getBaseUrlMethod() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("api.vk.com")
                .addPathSegment("method");
    }

    public Map<String, String> setGroupMap(final User user) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("user_id", user.getId());
        parameters.put("access_token", user.getAccess_token());
        parameters.put("filter", "admin");
        parameters.put("bool", "1");
        return parameters;
    }

    public Map<String, String> createAlbumMap(final User user, final Album album) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", user.getAccess_token());
        parameters.put("title", album.getName());
        parameters.put("group_id", album.getGroup_id());
        parameters.put("description", album.getDescription());
        parameters.put("upload_by_admins_only", "1");
        return parameters;
    }

    public Map<String, String> selectAlbumMap(final User user, final String group_id) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", user.getAccess_token());
        parameters.put("owner_id", group_id);
        parameters.put("need_covers", "1");
        return parameters;
    }

    public Map<String, String> setValueForGroupIdMap(final String group_id, final User user) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", user.getAccess_token());
        parameters.put("group_id", group_id);
        return parameters;
    }

    public Map<String, String> getUploadUrlMap(final User user, final String group_id, final String album_id) {
        Map<String, String> map = new HashMap<>();
        map.put("access_token", user.getAccess_token());
        map.put("album_id", album_id);
        map.put("group_id", group_id);
        return map;
    }

    public RequestBody requestBodyForUploadServer(File file) {
        return RequestBody.create(MediaType.parse(file.getName()), file);
    }

    public Map<String, String> getSavePhotoMap(UploadToServer upload) {
        Map<String, String> map = new HashMap<>();
        map.put("access_token", upload.getAccess_token());
        map.put("album_id", upload.getAlbum_id());
        map.put("group_id", upload.getGroup_id());
        map.put("server", upload.getServer());
        map.put("photos_list", upload.getPhotos_list());
        map.put("hash", upload.getHash());
        return map;
    }
}
