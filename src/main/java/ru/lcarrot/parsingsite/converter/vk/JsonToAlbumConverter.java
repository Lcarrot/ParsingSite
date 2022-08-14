package ru.lcarrot.parsingsite.converter.vk;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Album;

@Component
public class JsonToAlbumConverter implements Converter<JsonNode, Album> {

    @Override
    public Album convert(JsonNode jsonNode) {
        String id = jsonNode.get("id").asText();
        String owner_id = String.valueOf(Math.abs(jsonNode.get("owner_id").asInt()));
        String title = jsonNode.get("title").asText();
        String description = jsonNode.get("description").asText();
        return Album.builder().id(id).group_id(owner_id).name(title).description(description).build();
    }
}
