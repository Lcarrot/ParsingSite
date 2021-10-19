package ru.lcarrot.parsingsite.service.parse;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParseServiceManager {

    private final List<ParseService> parseServices;

    public ParseServiceManager(List<ParseService> parseServices) {
        this.parseServices = parseServices;
    }

    public ParseService getParseServiceByName(String name) {
        return parseServices.stream()
                .filter(service -> service.getSiteName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(name + " service can't find"));
    }

    public List<String> getAllServices() {
        return parseServices.stream().map(ParseService::getSiteName).collect(Collectors.toList());
    }
}
