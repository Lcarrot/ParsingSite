package ru.lcarrot.parsingsite.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.dto.UploadToServer;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.repository.VkJsonRepository;
import ru.lcarrot.parsingsite.security.authentication.TokenAuthentication;
import ru.lcarrot.parsingsite.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class VkService {

    private final VkJsonRepository vkRepository;
    private final ConversionService conversionService;

    public VkService(VkJsonRepository vkRepository,
        ConversionService conversionService) {
        this.vkRepository = vkRepository;
        this.conversionService = conversionService;
    }

    public User login(String code) {
        User user = conversionService.convert(vkRepository.login(code), User.class);
        SecurityContextHolder.getContext().setAuthentication(new TokenAuthentication(user, true));
        return user;
    }

    public List<Group> getGroups(User user) {
        JsonNode jsonNode = vkRepository.getGroups(user);
        int count = vkRepository.getGroups(user).get("count").asInt();
        if (count > 100_000) {
            return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(jsonNode.get("items").elements(),
                        Spliterator.CONCURRENT
                    ),
                    true
                )
                .map(x -> getGroupById(x.asText(), user))
                .collect(Collectors.toList());
        }
        else {
            List<Group> groupList = new ArrayList<>();
            for (Iterator<JsonNode> it = jsonNode.get("items").elements(); it.hasNext(); ) {
                JsonNode node = it.next();
                groupList.add(getGroupById(node.asText(), user));
            }
            return groupList;
        }
    }

    public Group getGroupById(String groupId, User user) {
        return conversionService.convert(vkRepository.getGroupById(groupId, user), Group.class);
    }

    public Album createAlbum(User user, Album album) {
        return conversionService.convert(vkRepository.createAlbum(user, album), Album.class);
    }

    public List<Album> getAlbums(User user, String group_id) throws IOException {
        JsonNode jsonNode = vkRepository.getAlbums(user, group_id);
        int count = jsonNode.get("count").asInt();
        if (count > 100_000) {
            return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(jsonNode.get("items").elements(),
                        Spliterator.CONCURRENT
                    ),
                    true
                )
                .map(o -> conversionService.convert(o, Album.class))
                .collect(Collectors.toList());
        }
        else {
            List<Album> albumList = new ArrayList<>();
            for (Iterator<JsonNode> it = jsonNode.get("items").elements(); it.hasNext(); ) {
                JsonNode next = it.next();
                albumList.add(conversionService.convert(next, Album.class));
            }
            return albumList;
        }
    }

    @SneakyThrows
    public void savePhotoInAlbum(final SavePhoto savePhoto) {
        File image = FileUtils.getImageByHref(savePhoto.getFolder(), savePhoto.getProduct().getImageHref());
        JsonNode node = vkRepository.uploadPhotoInServer(savePhoto, image);
        UploadToServer upload = conversionService.convert(node, UploadToServer.class);
        assert upload != null;
        upload.setDescription(savePhoto.getProduct().getDescription());
        upload.setAccess_token(savePhoto.getUser_access_token());
        JsonNode node1 = vkRepository.getSavePhotoUrl(upload);
        System.out.println(node1.asText());
        image.delete();
    }

    public String getUploadUrl(User user, String group_id, String album_id) {
        return vkRepository.getUploadUrl(user, group_id, album_id);
    }
}
