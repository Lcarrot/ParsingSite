package ru.lcarrot.parsingsite.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.dto.UploadToServer;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.User;

import java.io.File;
import java.net.URL;

@Component
public class VkJsonApiService {

  private final JsonApiHelper template;

  @Value("${client_secret}")
  private String client_secret;

  @Value("${client_id}")
  private String client_id;

  @Value("${domain}")
  private String domain;

  @Value("${vk.api.version}")
  private String vk_version;

  public VkJsonApiService(JsonApiHelper template) {
    this.template = template;
  }

  public JsonNode login(String code) {
    URL url = new HttpUrl.Builder()
        .scheme("https")
        .host("oauth.vk.com")
        .addPathSegment("access_token")
        .addQueryParameter("client_id", client_id)
        .addQueryParameter("client_secret", client_secret)
        .addQueryParameter("redirect_uri", domain + "/vk/signIn")
        .addQueryParameter("code", code).build().url();
    return template.getJsonNodeFromGetQuery(url);
  }

  @SneakyThrows
  public JsonNode getGroups(User user) {
    URL url = getApiBaseUrlMethod("groups.get").
        addQueryParameter("user_id", user.getId()).
        addQueryParameter("access_token", user.getAccess_token()).
        addQueryParameter("filter", "admin").
        addQueryParameter("bool", "1").build().url();
    return template.getJsonNodeFromGetQuery(url).get("response");
  }

  public JsonNode getGroupById(String groupId, User user) {
    URL url = getApiBaseUrlMethod("groups.getById").
        addQueryParameter("access_token", user.getAccess_token()).
        addQueryParameter("group_id", groupId).build().url();
    return template.getJsonNodeFromGetQuery(url).get("response").elements().next();
  }

  @SneakyThrows
  public JsonNode createAlbum(User user, Album album) {
    URL url = getApiBaseUrlMethod("photos.createAlbum").
        addQueryParameter("access_token", user.getAccess_token()).
        addQueryParameter("title", album.getName()).
        addQueryParameter("group_id", album.getGroup_id()).
        addQueryParameter("description", album.getDescription()).
        addQueryParameter("upload_by_admins_only", "1").build().url();
    return template.getJsonNodeFromGetQuery(url).get("response");
  }

  public JsonNode getAlbums(User user, String group_id) {
    URL url = getApiBaseUrlMethod("photos.getAlbums")
        .addQueryParameter("access_token", user.getAccess_token())
        .addQueryParameter("owner_id", "-" + group_id)
        .addQueryParameter("need_covers", "1").build().url();
    return template.getJsonNodeFromGetQuery(url).get("response");

  }

  @SneakyThrows
  public JsonNode uploadPhotoOnServer(SavePhoto savePhoto, File image) {
    URL url = new URL(savePhoto.getUpload_url());
    RequestBody requestBody = new MultipartBuilder()
        .type(MultipartBuilder.FORM)
        .addFormDataPart("file", image.getName(),
            RequestBody.create(MediaType.parse("image/png"), image)
        )
        .addFormDataPart("album_id", savePhoto.getAlbum_id())
        .addFormDataPart("group_id", savePhoto.getGroup_id())
        .build();
    return template.getJsonNodeFromPostQuery(url, requestBody);
  }

  public JsonNode getSavePhotoUrl(UploadToServer upload) {
    URL url = getApiBaseUrlMethod("photos.save").
        addQueryParameter("access_token", upload.getAccess_token()).
        addQueryParameter("album_id", upload.getAlbum_id()).
        addQueryParameter("group_id", upload.getGroup_id()).
        addQueryParameter("server", upload.getServer()).
        addQueryParameter("photos_list", upload.getPhotos_list()).
        addQueryParameter("hash", upload.getHash()).
        addQueryParameter("caption", upload.getDescription()).build().url();
    return template.getJsonNodeFromGetQuery(url);
  }

  @SneakyThrows
  public String getUploadUrl(User user, String group_id, String album_id) {
    URL url = getApiBaseUrlMethod("photos.getUploadServer").
        addQueryParameter("access_token", user.getAccess_token()).
        addQueryParameter("album_id", album_id).
        addQueryParameter("group_id", group_id).build().url();
    return template.getJsonNodeFromGetQuery(url).get("response").get("upload_url").asText();
  }

  private HttpUrl.Builder getApiBaseUrlMethod(final String methodName) {
    return new HttpUrl.Builder()
        .scheme("https")
        .host("api.vk.com")
        .addPathSegment("method")
        .addPathSegment(methodName)
        .addQueryParameter("v", vk_version);
  }
}
