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
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class SimaLandItemToProductConverter implements Converter<Element, Product> {

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
        FileUtils.downloadFromInternet(file, new URL(image.attr("src")));
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append(Objects.requireNonNull(element.getElementsByClass("_3JJFA").first()).text()).append("\n");
        stringBuilder.append("обычная цена = ").append(Objects.requireNonNull(element.getElementsByClass("ObDrR").first()).text()).append("\n");
        stringBuilder.append(Objects.requireNonNull(element.getElementsByClass("_2vjg5 _3Xn-P _2d8jp").first()).text()).append("\n");
        element.getElementsByClass("Iaewc _168Gz _3MTJl").forEach(x -> stringBuilder.append(x.text()).append("\n"));
        return Product.builder().image(file).description(stringBuilder.toString()).build();
    }
}
