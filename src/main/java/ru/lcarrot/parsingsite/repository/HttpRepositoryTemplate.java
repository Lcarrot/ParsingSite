package ru.lcarrot.parsingsite.repository;

import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.util.OkHttpUtils;

/**
 * @author l.tyshchenko
 */

@Component
public class HttpRepositoryTemplate {

  private final ObjectMapper objectMapper;

  @SneakyThrows
   public JsonNode getJsonNodeFromGetQuery(URL url) {
     Call call = OkHttpUtils.getCallFromGetQuery(url);
     try (ResponseBody responseBody = call.execute().body()) {
        return objectMapper.readTree(responseBody.string());
     }
     finally {
       call.cancel();
     }
   }

   @SneakyThrows
  public JsonNode getJsonNodeFromPostQuery(URL url, RequestBody requestBody) {
    Call call = OkHttpUtils.getResponseFromPostQuery(url, requestBody);
    try (ResponseBody responseBody = call.execute().body()) {
      return objectMapper.readTree(responseBody.string());
    }
    finally {
      call.cancel();
    }
  }

  public HttpRepositoryTemplate(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }
}
