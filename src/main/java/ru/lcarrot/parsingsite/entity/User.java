package ru.lcarrot.parsingsite.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String id;
    private String access_token;
    private List<Group> groupList;
    @Builder.Default
    private List<CompletableFuture<Boolean>> tasks = new ArrayList<>();
}
