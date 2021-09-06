package ru.lcarrot.parsingsite.entity;

import lombok.Data;

import java.io.File;

@Data
public class Product {

    private String name;
    private File image;
    private Description description;
}
