package ru.lcarrot.parsingsite.util;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.springframework.stereotype.Component;

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
