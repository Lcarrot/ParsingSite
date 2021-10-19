package ru.lcarrot.parsingsite.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.ResponseBody;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.dto.SavePhoto;
import ru.lcarrot.parsingsite.entity.Album;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.repository.VkRepository;
import ru.lcarrot.parsingsite.security.authentication.TokenAuthentication;
import ru.lcarrot.parsingsite.util.FileUtils;
import ru.lcarrot.parsingsite.util.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class VkService {

    private final VkRepository vkRepository;

    public VkService(VkRepository vkRepository) {
        this.vkRepository = vkRepository;
    }

    public User login(String code) throws IOException {
        User user = vkRepository.login(code);
        SecurityContextHolder.getContext().setAuthentication(new TokenAuthentication(user, true));
        return user;
    }

    public List<Group> getGroups(User user) {
        return vkRepository.getGroupList(user);
    }

    public Album createAlbum(User user, Album album) {
        return vkRepository.createAlbum(user, album);
    }

    public List<Album> getAlbums(User user, String group_id) throws IOException {
        return vkRepository.getAlbums(user, group_id);
    }

    public void savePhotoInAlbum(final SavePhoto savePhoto) {
        String folder = savePhoto.getGroup_id() + savePhoto.getAlbum_id();
        File image = FileUtils.getImageByHref(folder, savePhoto.getProduct().getImageHref());
        vkRepository.savePhoto(savePhoto, image);
        image.delete();
    }

    public String getUploadUrl(User user, String group_id, String album_id) {
        return vkRepository.getUploadUrl(user, group_id, album_id);
    }
}
