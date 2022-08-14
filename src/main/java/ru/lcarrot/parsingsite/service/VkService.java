package ru.lcarrot.parsingsite.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.api.VkJsonApiService;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.dto.UploadToServer;
import ru.lcarrot.parsingsite.entity.*;
import ru.lcarrot.parsingsite.parser.Parser;
import ru.lcarrot.parsingsite.parser.ParserManager;
import ru.lcarrot.parsingsite.util.FileUtils;

import static ru.lcarrot.parsingsite.util.HtmParseUtils.getDocumentPageFromSite;

@Log
@Component
public class VkService {

  private final VkJsonApiService vkApiService;
  private final ParserManager parserManager;
  private final UserService userService;
  private final ConversionService conversionService;

  public VkService(VkJsonApiService vkApiService,
      ParserManager parserManager, UserService userService,
      ConversionService conversionService) {
    this.vkApiService = vkApiService;
    this.parserManager = parserManager;
    this.userService = userService;
    this.conversionService = conversionService;
  }

  public void login(String code) {
    User user = conversionService.convert(vkApiService.login(code), User.class);
    userService.login(user);
  }

  public List<Group> getGroups(User user) {
    JsonNode jsonNode = vkApiService.getGroups(user);
    List<Group> groupList = new ArrayList<>();
    for (Iterator<JsonNode> it = jsonNode.get("items").elements(); it.hasNext(); ) {
      JsonNode node = it.next();
      Group group = conversionService.convert(vkApiService.getGroupById(node.asText(), user),
          Group.class
      );
      groupList.add(group);
    }
    user.setGroupList(groupList);
    return groupList;
  }

  public Album createAlbum(User user, Album album) {
    return conversionService.convert(vkApiService.createAlbum(user, album), Album.class);
  }

  public List<Album> getAlbums(User user, String group_id, boolean reload) {
    Optional<Group> group = user.getGroupList()
        .stream()
        .filter(x -> x.getId().equals(group_id))
        .findFirst();
    List<Album> albums;
    if (group.isPresent() && group.get().getAlbums() != null && !reload) {
      albums = group.get().getAlbums();
    }
    else {
      JsonNode jsonNode = vkApiService.getAlbums(user, group_id);
      albums = new ArrayList<>();
      for (Iterator<JsonNode> it = jsonNode.get("items").elements(); it.hasNext(); ) {
        JsonNode next = it.next();
        albums.add(conversionService.convert(next, Album.class));
      }
    }
    return albums;
  }

  @SneakyThrows
  public void savePhotoInAlbum(final SavePhoto savePhoto) {
    File image = FileUtils.getImageByHref(savePhoto.getFolder(),
        savePhoto.getProduct().getImageHref()
    );
    JsonNode node = vkApiService.uploadPhotoOnServer(savePhoto, image);
    UploadToServer upload = conversionService.convert(node, UploadToServer.class);
    if (upload != null) {
      upload.setDescription(savePhoto.getProduct().getDescription());
      upload.setAccess_token(savePhoto.getUser_access_token());
      JsonNode node1 = vkApiService.getSavePhotoUrl(upload);
      log.info("save photo : " + node1.asText());
      Files.delete(image.toPath());
    }
  }

  public String getUploadUrl(User user, String group_id, String album_id) {
    return vkApiService.getUploadUrl(user, group_id, album_id);
  }

  @SneakyThrows
  public ParseInfo getParseTask(String site, User user, String group_id, String album_id,
      String url) {
    Parser parseService = parserManager.getParseServiceByName(site);
    String upload_url = getUploadUrl(user, group_id, album_id);
    int count = parseService.getPageCount(getDocumentPageFromSite(url));
    AtomicInteger pageParsedCount = new AtomicInteger(0);
    Path folder = Paths.get(group_id, album_id);
    Files.createDirectories(folder);
    CompletableFuture<?> future = CompletableFuture.runAsync(() -> {
      Disposable disposable = Flowable.range(1, count)
          .map(pageNumber -> {
            pageParsedCount.set(pageNumber);
            return parseService.getDocumentPageByNumber(url, pageNumber);
          })
          .map(parseService::getProducts).subscribeOn(Schedulers.computation()).subscribe(list -> {
            for (Product product : list) {
              savePhotoInAlbum(SavePhoto.builder()
                  .album_id(album_id)
                  .folder(folder)
                  .user_access_token(user.getAccess_token())
                  .product(product)
                  .upload_url(upload_url)
                  .group_id(group_id)
                  .build());
            }
          });
    });
    return ParseInfo.builder()
        .completableFuture(future)
        .album_id(album_id)
        .url(url)
        .count(pageParsedCount)
        .allPagesCount(count)
        .build();
  }
}
