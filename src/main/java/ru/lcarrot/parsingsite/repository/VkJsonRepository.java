package ru.lcarrot.parsingsite.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.RequestBody;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.dto.UploadToServer;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.File;
import java.net.URL;

@Component
public class VkJsonRepository {

  private final VkApiUtils vkApiUtils;
  private final HttpRepositoryTemplate template;

  public VkJsonRepository(VkApiUtils vkApiUtils,
      HttpRepositoryTemplate template) {
    this.vkApiUtils = vkApiUtils;
    this.template = template;
  }

  public JsonNode login(String code) {
    URL url = vkApiUtils.getOauthURL(code);
    return template.getJsonNodeFromGetQuery(url);
  }

  @SneakyThrows
  public JsonNode getGroups(User user) {
    URL url = vkApiUtils.getGroupUrl(user);
    return template.getJsonNodeFromGetQuery(url).get("response");
  }

  public JsonNode getGroupById(String groupId, User user) {
    URL url = vkApiUtils.setValueForGroupIdMap(groupId, user);
    return template.getJsonNodeFromGetQuery(url).get("response").elements().next();
  }

  @SneakyThrows
  public JsonNode createAlbum(User user, Album album) {
    URL url = vkApiUtils.createAlbumUrl(user, album);
    return template.getJsonNodeFromGetQuery(url).get("response");
  }

  public JsonNode getAlbums(User user, String group_id) {
    URL url = vkApiUtils.getAlbumUrl(user, group_id);
    return template.getJsonNodeFromGetQuery(url).get("response");

  }

  @SneakyThrows
  public JsonNode uploadPhotoInServer(SavePhoto savePhoto, File image) {
    URL url = new URL(savePhoto.getUpload_url());
    RequestBody requestBody = vkApiUtils.requestBodyForUploadServer(image, savePhoto.getAlbum_id(),
        savePhoto.getGroup_id()
    );
    return template.getJsonNodeFromPostQuery(url, requestBody);
  }

  public JsonNode getSavePhotoUrl(UploadToServer upload) {
    URL url1 = vkApiUtils.getSavePhotoUrl(upload);
    return template.getJsonNodeFromGetQuery(url1);
  }

  @SneakyThrows
  public String getUploadUrl(User user, String group_id, String album_id) {
    URL url = vkApiUtils.getUploadServerUrl(user, group_id, album_id);
    return template.getJsonNodeFromGetQuery(url).get("response").get("upload_url").asText();
  }

}
