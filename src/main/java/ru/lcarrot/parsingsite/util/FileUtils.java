package ru.lcarrot.parsingsite.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
public class FileUtils {

    public void downloadFromInternet(File file, URL url) throws IOException {
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        long transfer_bytes = Long.MAX_VALUE;
        while (transfer_bytes == Long.MAX_VALUE) {
            transfer_bytes = fos.getChannel().transferFrom(rbc, fos.getChannel().position(), Long.MAX_VALUE);
        }
    }
}
