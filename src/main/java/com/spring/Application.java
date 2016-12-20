package com.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("${spring.config.location}")
public class Application{

    private static Class[] sources = {Application.class};

    public static void main(String[] args) throws Exception {
        SpringApplication.run(sources, args);
    }

}
