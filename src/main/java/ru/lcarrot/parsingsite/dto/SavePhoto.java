package ru.lcarrot.parsingsite.dto;

import lombok.Builder;
import lombok.Data;
import ru.lcarrot.parsingsite.entity.Product;

@Data
@Builder
public class SavePhoto {

    private String user_access_token;
    private Product product;
    private String upload_url;
    private String album_id;
    private String group_id;
}
