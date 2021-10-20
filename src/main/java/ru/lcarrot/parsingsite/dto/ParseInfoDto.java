package ru.lcarrot.parsingsite.dto;

import lombok.Builder;
import lombok.Data;
import ru.lcarrot.parsingsite.entity.ParseInfo;

@Data
@Builder
public class ParseInfoDto {

    private String album_id;
    private Integer count;
    private String url;
    private Integer allPagesCount;

    public static ParseInfoDto to(ParseInfo parseInfo) {
        return ParseInfoDto.builder()
                .count(parseInfo.getCount().get())
                .album_id(parseInfo.getAlbum_id())
                .url(parseInfo.getUrl())
                .allPagesCount(parseInfo.getAllPagesCount())
                .build();
    }
}
