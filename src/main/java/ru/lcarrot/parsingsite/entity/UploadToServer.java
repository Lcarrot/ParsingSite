package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadToServer {

    private String access_token;
    private String album_id;
    private String group_id;
    private String server;
    private String photos_list;
    private String hash;
}
