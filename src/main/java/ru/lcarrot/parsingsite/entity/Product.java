package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
public class Product {

    private String name;
    private File image;
    private String description;
}
