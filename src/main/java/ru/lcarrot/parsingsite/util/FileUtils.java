package ru.lcarrot.parsingsite.util;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

@Log
public class FileUtils {

    public static void downloadImage(File file, URL url) throws IOException {
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(file)){
            long transfer_bytes = Long.MAX_VALUE;
            while (transfer_bytes == Long.MAX_VALUE) {
                transfer_bytes = fos.getChannel().transferFrom(rbc, fos.getChannel().position(), Long.MAX_VALUE);
            }
        }
    }

    @SneakyThrows
    public static File getImageByHref(Path folder, String href) {
        String name = String.valueOf(ThreadLocalRandom.current().nextInt());
        Path path = Paths.get(String.valueOf(folder), name + ".jpg");
        File file = new File(String.valueOf(path));
        if (file.createNewFile()) {
            FileUtils.downloadImage(file, new URL(href));
        } else {
            log.log(Level.SEVERE, "Can't save file '" +  file + "'");
        }
        return file;
    }
}
