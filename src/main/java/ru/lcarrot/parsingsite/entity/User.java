package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String id;
    private String access_token;
    private String group_id;
}
