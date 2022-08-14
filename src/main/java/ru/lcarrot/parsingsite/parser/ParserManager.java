package ru.lcarrot.parsingsite.parser;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParserManager {

    private final List<Parser> parsers;

    public ParserManager(List<Parser> parsers) {
        this.parsers = parsers;
    }

    public Parser getParseServiceByName(String name) {
        return parsers.stream()
                .filter(service -> service.getSiteName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(name + " service can't find"));
    }

    public List<String> getAllServices() {
        return parsers.stream().map(Parser::getSiteName).collect(Collectors.toList());
    }
}
