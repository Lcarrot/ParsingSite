package ru.lcarrot.parsingsite.service.parse;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.lcarrot.parsingsite.converters.SimaLandItemToProductConverter;
import ru.lcarrot.parsingsite.entity.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class SimaLandService extends AbstractParseService {

    @Autowired
    private SimaLandItemToProductConverter productConverter;


    private SimaLandService() {
        super("simaland");
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
        return Integer.parseInt(Objects.requireNonNull(document.getElementsByClass("_3h29A").first()).text());
    }

    @Override
    public Document getDocumentPageFromSite(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36")
                .referrer("https://www.google.com")
                .get();
    }

    @SneakyThrows
    @Override
    public Document getDocumentPageByNumber(String url, int page) {
        int lastIndex;
        if (url.contains("?")) {
            lastIndex = url.lastIndexOf("?");
        }
        else {
            lastIndex = url.length() - 1;
        }
        return getDocumentPageFromSite(url.substring(0, lastIndex).concat("p" + page));
    }
}
