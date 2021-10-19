package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {

    private String name;
    private String imageHref;
    private String description;
}
