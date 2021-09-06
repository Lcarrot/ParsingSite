package ru.lcarrot.parsingsite.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Group;
import ru.lcarrot.parsingsite.entity.User;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class VkService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VkApiUtils vkApiUtils;

    public User login(Response response) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(response.body().string());
        String id = jsonNode.get("user_id").asText();
        String access_token = jsonNode.get("access_token").asText();
        return User.builder().id(id).access_token(access_token).build();
    }

    public List<Group> getGroups(Response response, User user) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(response.body().string());
        jsonNode.get("response").get("items").elements();
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(jsonNode.get("response").get("items").elements(), Spliterator.ORDERED),
                false).
                parallel().
                map(x -> {
                    URL url = vkApiUtils.getUrlForMethod("groups.getById", getSecondMap(x.asText(), user));
                    Request request1 = new Request.Builder().url(url).build();
                    int id = 0;
                    String name = null, photo = null, screen_name = null;
                    try {
                        Response response1 = new OkHttpClient().newCall(request1).execute();
                        JsonNode node = objectMapper.readTree(response1.body().string()).get("response").elements().next();
                        id = node.get("id").asInt();
                        name = node.get("name").asText();
                        photo = node.get("photo_100").asText();
                        screen_name = node.get("screen_name").asText();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Group.builder().id(id).name(name).photo(photo).screen_name(screen_name).build();
                }).collect(Collectors.toList());
    }

    private Map<String, String> getSecondMap(String group_id, User user) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", user.getAccess_token());
        parameters.put("group_id", group_id);
        return parameters;
    }
}
