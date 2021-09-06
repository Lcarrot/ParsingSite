package ru.lcarrot.parsingsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.lcarrot.parsingsite.util.VkApiUtils;

@SpringBootApplication
public class ParsingSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParsingSiteApplication.class, args);
    }
}
