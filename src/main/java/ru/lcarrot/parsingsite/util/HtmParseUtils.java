package ru.lcarrot.parsingsite.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class HtmParseUtils {

  public static Document getDocumentPageFromSite(String url) throws IOException {
    return Jsoup.connect(url)
        .userAgent(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36")
        .referrer("https://www.google.com")
        .get();
  }
}
