package ru.lcarrot.parsingsite.parser;

import org.jsoup.nodes.Document;
import ru.lcarrot.parsingsite.entity.Product;

import java.util.List;

public interface Parser {

    /*
     должен возвращать название сайта
     */
    String getSiteName();

    /*
    должен возвращать распарсенный список товаров
     */
    List<Product> getProducts(Document document);

    /*
    возвращает количество страниц товара на сайте (так же парсится)
     */
    int getPageCount(Document document);

    /*
    добавляет страницу в запрос и парсит его
     */
    Document getDocumentPageByNumber(String url, int page);
}
