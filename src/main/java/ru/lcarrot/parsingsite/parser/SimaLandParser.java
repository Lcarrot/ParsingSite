package ru.lcarrot.parsingsite.parser;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.converter.SimaLandProductConverter;
import ru.lcarrot.parsingsite.entity.Product;

import static ru.lcarrot.parsingsite.util.HtmParseUtils.getDocumentPageFromSite;

@Component
public class SimaLandParser implements Parser {

    private final SimaLandProductConverter productConverter;

    @Value("${simaland.service.name}")
    private String serviceName;

    public SimaLandParser(SimaLandProductConverter productConverter) {
        this.productConverter = productConverter;
    }

    @Override
    public String getSiteName() {
        return serviceName;
    }

    @Override
    public List<Product> getProducts(Document document) {
        Elements elements = document.getElementsByClass("catalog__item");
        List<Product> productList = new ArrayList<>();
        for (Element element: elements) {
            productList.add(productConverter.convert(element));
        }
        return productList;
    }

    @Override
    public int getPageCount(Document document) {
        return  document.getElementsByClass("_3h29A").stream()
                .map(value -> Integer.parseInt(value.text())).findFirst().orElse(1);
    }

    @SneakyThrows
    @Override
    public Document getDocumentPageByNumber(String url, int page) {
        int lastIndex;
        String filters = null;
        if (url.contains("?")) {
            lastIndex = url.lastIndexOf("?");
            filters = url.substring(lastIndex);
        }
        else {
            lastIndex = url.length() - 1;
        }
        String query = (url.substring(0, lastIndex).concat("p" + page)).concat("/");
        query = (filters != null) ? query.concat(filters) : query;
        return getDocumentPageFromSite(query);
    }
}
