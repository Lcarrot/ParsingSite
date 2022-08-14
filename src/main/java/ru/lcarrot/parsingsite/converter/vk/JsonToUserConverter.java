package ru.lcarrot.parsingsite.converter.vk;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.User;

@Component
public class JsonToUserConverter implements Converter<JsonNode, User> {


    @Override
    public User convert(JsonNode jsonNode) {
        String id = jsonNode.get("user_id").asText();
        String access_token = jsonNode.get("access_token").asText();
        return User.builder().id(id).access_token(access_token).build();
    }
}
