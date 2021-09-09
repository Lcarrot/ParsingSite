package ru.lcarrot.parsingsite.service.parse;

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
public class SimaLandService implements ParseService{

    @Autowired
    private SimaLandItemToProductConverter productConverter;

    @Override
    public String getSiteName() {
        return "simaland";
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
        return Integer.parseInt(Objects.requireNonNull(document.getElementsByClass("_338X8 _3h29A").first()).text());
    }

    @Override
    public Document getDocumentPageFromSite(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36")
                .referrer("https://www.google.com")
                .get();
    }

    @Override
    public Document getNextPage(int page) {
        return null;
    }
}
