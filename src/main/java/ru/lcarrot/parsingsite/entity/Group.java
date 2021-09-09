package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Group {
    private String id;
    private String name;
    private String photo;
    private String screen_name;
    private List<Album> albums;
}
