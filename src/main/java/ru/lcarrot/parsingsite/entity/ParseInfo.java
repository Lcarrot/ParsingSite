package ru.lcarrot.parsingsite.entity;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Builder
public class ParseInfo {

    private String album_id;
    private AtomicInteger count;
    private String url;
    private CompletableFuture<?> completableFuture;
    private Integer allPagesCount;
    private Path folder;
}
