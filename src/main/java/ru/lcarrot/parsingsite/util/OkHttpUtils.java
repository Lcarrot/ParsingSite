package ru.lcarrot.parsingsite.util;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class OkHttpUtils {

    public Response getResponseFromGetQuery(final URL url) throws IOException {
        return new OkHttpClient().newCall(new Request.Builder().url(url).build()).execute();
    }

    public Response getResponseFromPostQuery(final URL url, RequestBody requestBody) throws IOException {
        return new OkHttpClient().newCall(new Request.Builder().url(url).post(requestBody).build()).execute();
    }
}
