package ru.lcarrot.parsingsite.converters;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UrlToDocumentConverter implements Converter<String, Document> {


    @SneakyThrows
    @Override
    public Document convert(String s) {
        return Jsoup.connect("https://www.sima-land.ru/detskie-tovary-dlya-uhoda-i-gigieny/?per-page=20&sort=price&viewtype=list")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36")
                .referrer("https://www.google.com")
                .get();
    }
}
