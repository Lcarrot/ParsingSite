package ru.lcarrot.parsingsite.service.parse;

public abstract class AbstractParseService implements ParseService {

    protected String name;

    protected AbstractParseService(String name) {
        this.name = name;
    }

    @Override
    public String getSiteName() {
        return name;
    }


}
