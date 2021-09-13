package ru.lcarrot.parsingsite.util;

import com.squareup.okhttp.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class OkHttpUtils {


    public Call getCallFromGetQuery(final URL url) {
        return new OkHttpClient().newCall(new Request.Builder().url(url).build());
    }

    public Call getResponseFromPostQuery(final URL url, RequestBody requestBody) {
        return new OkHttpClient().newCall(new Request.Builder().url(url).post(requestBody).build());
    }
}
