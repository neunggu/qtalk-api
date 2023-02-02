package com.quack.talk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.quack.talk.*"})
@EnableScheduling
@PropertySource("classpath:application-${profile:dev}.properties")
public class QtalkApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QtalkApiApplication.class, args);
    }

}
