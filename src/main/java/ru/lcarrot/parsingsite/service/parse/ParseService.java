package ru.lcarrot.parsingsite.service.parse;

import org.jsoup.nodes.Document;
import ru.lcarrot.parsingsite.entity.Product;

import java.io.IOException;
import java.util.List;

public interface ParseService {

    String getSiteName();

    List<Product> getProducts(Document document);

    int getPageCount(Document document);

    Document getDocumentPageFromSite(String url) throws IOException;

    Document getDocumentPageByNumber(String url, int page);
}
