package ru.lcarrot.parsingsite.converters;

import lombok.SneakyThrows;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Product;
import ru.lcarrot.parsingsite.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SimaLandItemToProductConverter implements Converter<Element, Product> {

    @Autowired
    private FileUtils fileUtils;

    private final String dir = "images/";

    SimaLandItemToProductConverter() throws IOException {
        Files.createDirectories(Paths.get(dir));
    }

    @SneakyThrows
    @Override
    public Product convert(Element element) {
        Element image = element.getElementsByTag("img").first();
        File file = new File(String.valueOf(Paths.get(dir, ThreadLocalRandom.current().nextInt() + ".png")));
        file.createNewFile();
        assert image != null;
        fileUtils.downloadFromInternet(file, new URL(image.attr("src")));
        return Product.builder().image(file).description(element.text()).build();
    }
}
