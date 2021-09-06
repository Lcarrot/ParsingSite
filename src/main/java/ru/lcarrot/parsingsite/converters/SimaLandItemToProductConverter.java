package ru.lcarrot.parsingsite.converters;

import org.jsoup.nodes.Element;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Product;

@Component
public class SimaLandItemToProductConverter implements Converter<Element, Product> {

    @Override
    public Product convert(Element element) {
            Element image = element.getElementsByTag("img").first();
            System.out.println(image.attr("src"));
            System.out.println(element.text());
        return null;
    }
}
