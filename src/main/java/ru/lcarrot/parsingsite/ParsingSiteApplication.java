package ru.lcarrot.parsingsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.lcarrot.parsingsite.util.VkApiUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
public class ParsingSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParsingSiteApplication.class, args);
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }
}
