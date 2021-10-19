package ru.lcarrot.parsingsite.converters;

import lombok.SneakyThrows;
import org.jsoup.nodes.Element;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Product;
import java.util.Objects;

@Component
public class SimaLandHtmlToProductConverter implements Converter<Element, Product> {

    @SneakyThrows
    @Override
    public Product convert(Element element) {
        Element imageElement = element.getElementsByTag("img").first();
        StringBuffer stringBuilder = new StringBuffer();
        stringBuilder.append(Objects.requireNonNull(element.getElementsByClass("_3JJFA").first()).text()).append("\n");
        stringBuilder.append("обычная цена = ").append(Objects.requireNonNull(element.getElementsByClass("ObDrR").first()).text()).append("\n");
        stringBuilder.append(Objects.requireNonNull(element.getElementsByClass("_2vjg5 _3Xn-P _2d8jp").first()).text()).append("\n");
        element.getElementsByClass("Iaewc _168Gz _3MTJl").forEach(x -> stringBuilder.append(x.text()).append("\n"));
        assert imageElement != null;
        return Product.builder().description(stringBuilder.toString()).imageHref(imageElement.attr("src")).build();
    }
}
