package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Album {

    private String id;
    private String name;
    private String group_id;
    private String description;
}
