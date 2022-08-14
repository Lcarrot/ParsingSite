package ru.lcarrot.parsingsite.api;

import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * @author l.tyshchenko
 */

@Component
public class JsonApiHelper {

  private final ObjectMapper objectMapper;

  public JsonApiHelper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @SneakyThrows
   public JsonNode getJsonNodeFromGetQuery(URL url) {
     Call call = new OkHttpClient().newCall(new Request.Builder().url(url).build());
     try (ResponseBody responseBody = call.execute().body()) {
        return objectMapper.readTree(responseBody.string());
     }
     finally {
       call.cancel();
     }
   }

   @SneakyThrows
  public JsonNode getJsonNodeFromPostQuery(URL url, RequestBody requestBody) {
    Call call = new OkHttpClient().newCall(new Request.Builder().url(url).post(requestBody).build());
    try (ResponseBody responseBody = call.execute().body()) {
      return objectMapper.readTree(responseBody.string());
    }
    finally {
      call.cancel();
    }
  }
}
