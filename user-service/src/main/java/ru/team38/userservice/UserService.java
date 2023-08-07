package ru.team38.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.team38.common.aspects.LoggerAspect;

@SpringBootApplication
@EnableAsync
@Import({LoggerAspect.class})
public class UserService {
    public static void main(String[] args) {
        SpringApplication.run(UserService.class);
    }
}
