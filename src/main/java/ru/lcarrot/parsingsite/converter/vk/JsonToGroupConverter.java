package ru.lcarrot.parsingsite.converter.vk;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Group;

@Component
public class JsonToGroupConverter implements Converter<JsonNode, Group> {

    @Override
    public Group convert(JsonNode node) {
        return Group.builder().id(node.get("id").asText())
                .name(node.get("name").asText())
                .photo(node.get("photo_100").asText())
                .screen_name(node.get("screen_name").asText())
                .build();
    }
}
