package ru.lcarrot.parsingsite.converters;

import lombok.SneakyThrows;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.entity.Product;

@Component
public class SimaLandHtmlToProductConverter implements Converter<Element, Product> {


    String[] fields = new String[]{"_1IJDX", "_2y7gZ _1zR85 _1alHI", "UDKFU", "Iaewc _168Gz _3MTJl"};

    @SneakyThrows
    @Override
    public Product convert(Element element) {
        Element imageElement = element.getElementsByTag("img").first();
        StringBuffer stringBuilder = new StringBuffer();
        for (String field : fields) {
            Elements text = element.getElementsByClass(field);
            if (field.equals("_1IJDX")) {
                Element element1 = text.first();
                if (element1 != null) {
                    addUrlToDescription(element1, stringBuilder);
                    Element nameElement = element1.getElementsByClass("_3JJFA").first();
                    if (nameElement != null) {
                        stringBuilder.append(nameElement.text()).append("\n");
                    }
                }
            } else if (field.equals("_2y7gZ _1zR85 _1alHI")) {
                Element element1 = text.first();
                if (element1 != null) {
                    stringBuilder.append("обычная цена = ").append(element1.text()).append("\n");
                }
            } else {
                addTextToDescription(text, stringBuilder, field);
            }
        }
        assert imageElement != null;
        return Product.builder().description(stringBuilder.toString()).imageHref(imageElement.attr("src")).build();
    }

    private void addTextToDescription(Elements elements, StringBuffer stringBuilder, String htmlClass) {
        elements.forEach(element -> element.getElementsByClass(htmlClass)
                .forEach(spanElement -> spanElement.getElementsByTag("span")
                        .forEach(classElement -> stringBuilder.append(classElement.text()).append("\n"))));
    }

    private void addUrlToDescription(Element element, StringBuffer stringBuilder) {
        Element hrefElement = element.getElementsByClass("_2eMbQ dS9nC").first();
        if (hrefElement != null) {
            stringBuilder.append("https://www.sima-land.ru")
                    .append(hrefElement.getElementsByTag("a")
                            .first().attr("href"))
                    .append("\n");
        }
    }
}
