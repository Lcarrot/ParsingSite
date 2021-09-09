package ru.lcarrot.parsingsite.dto;

import lombok.Data;
import ru.lcarrot.parsingsite.entity.Album;

@Data
public class AlbumDto {

    private String name;
    private String description;

    public Album to() {
        return Album.builder()
                .name(name)
                .description(description)
                .build();
    }
}
