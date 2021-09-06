package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Group {
    private int id;
    private String name;
    private String photo;
    private String screen_name;
}
