package ru.lcarrot.parsingsite.util;

import com.squareup.okhttp.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;

@Component
public class VkApiUtils {

    @Value("${client_secret}")
    private String client_secret;

    @Value("${client_id}")
    private String client_id;

    @Value("${domain}")
    private String domain;

    private String vk_version = "5.131";

    public URL getUrlForMethod(String method, Map<String, String> parameters) {
        HttpUrl.Builder httpUrl = getBaseUrlMethod().addPathSegment(method);
        for (Map.Entry<String, String> parameter: parameters.entrySet()) {
            httpUrl.addQueryParameter(parameter.getKey(), parameter.getValue());
        }
        return httpUrl.addQueryParameter("v", vk_version).build().url();
    }

    public URL getAuthURL(String code) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("oauth.vk.com")
                .addPathSegment("access_token")
                .addQueryParameter("client_id", client_id)
                .addQueryParameter("client_secret", client_secret)
                .addQueryParameter("redirect_uri", domain + "/getAccessToken")
                .addQueryParameter("code", code).build().url();
    }

    private HttpUrl.Builder getBaseUrlMethod() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("api.vk.com")
                .addPathSegment("method");
    }
}
