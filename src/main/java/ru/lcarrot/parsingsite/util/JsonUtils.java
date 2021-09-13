package ru.lcarrot.parsingsite.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonUtils {

    @Autowired
    private ObjectMapper objectMapper;

    public JsonNode get(ResponseBody response, String ... parameters) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(response.string());
        for (String parameter: parameters) {
            jsonNode = jsonNode.get(parameter);
        }
        return jsonNode;
    }
}
