package ru.lcarrot.parsingsite.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ru.lcarrot.parsingsite.dto.ParseInfoDto;
import ru.lcarrot.parsingsite.entity.ParseInfo;
import ru.lcarrot.parsingsite.entity.User;

@Service
public class UserParseTaskService {

  private final Map<String, List<ParseInfo>> userTasks = new ConcurrentHashMap<>();

  public void addTasksToUser(String userId, ParseInfo parseInfo) {
    List<ParseInfo> parseInfos = userTasks.computeIfAbsent(userId,id -> new ArrayList<>());
    parseInfos.add(parseInfo);
  }

  public List<ParseInfoDto> getCurrentUserTasks(User user) {
    List<ParseInfo> tasks = userTasks.get(user.getId());
    if (tasks == null) return Collections.emptyList();
    tasks.removeIf(task -> task.getCompletableFuture().isDone());
    return tasks
        .stream()
        .map(ParseInfoDto::to)
        .collect(Collectors.toList());
  }
}
