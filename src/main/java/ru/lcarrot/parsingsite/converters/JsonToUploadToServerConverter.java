package ru.lcarrot.parsingsite.converters;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.dto.UploadToServer;

@Component
public class JsonToUploadToServerConverter implements Converter<JsonNode, UploadToServer> {

    @Override
    public UploadToServer convert(JsonNode node) {
        return UploadToServer.builder()
                .server(node.get("server").asText())
                .album_id(node.get("aid").asText())
                .photos_list(node.get("photos_list").asText())
                .hash(node.get("hash").asText())
                .group_id(node.get("gid").asText())
                .build();
    }
}
