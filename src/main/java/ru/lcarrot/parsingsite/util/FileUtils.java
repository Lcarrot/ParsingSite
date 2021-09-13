package ru.lcarrot.parsingsite.util;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;

@Component
public class FileUtils {

    public void downloadFromInternet(File file, URL url) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        InputStream inputStream = url.openStream();
        byte[] bytes = new byte[1024];
        int byteRead;
        while ((byteRead = inputStream.read(bytes, 0, 1024)) != -1) {
            outputStream.write(bytes, 0, byteRead);
        }
    }
}
