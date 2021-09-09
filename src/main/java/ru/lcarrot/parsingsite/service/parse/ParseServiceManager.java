package ru.lcarrot.parsingsite.service.parse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParseServiceManager {

    @Autowired
    private List<ParseService> parseServices;

    public ParseService getParseServiceByName(String name) {
        for (ParseService service: parseServices) {
            if (service.getSiteName().equals(name)) return service;
        }
        throw new IllegalStateException(name + " service can't find");
    }
}
