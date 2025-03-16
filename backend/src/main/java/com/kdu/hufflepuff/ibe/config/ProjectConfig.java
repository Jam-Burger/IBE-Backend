package com.kdu.hufflepuff.ibe.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Import({SecurityConfig.class, WebConfig.class, ModelMapperConfig.class})
@EntityScan(basePackages = "com.kdu.hufflepuff.ibe.model.entity")
@EnableJpaRepositories(basePackages = "com.kdu.hufflepuff.ibe.repository")
@ComponentScan(basePackages = {
    "com.kdu.hufflepuff.ibe.mapper",
    "com.kdu.hufflepuff.ibe.service.impl",
    "com.kdu.hufflepuff.ibe.exception",
    "com.kdu.hufflepuff.ibe.controller"
})
public class ProjectConfig {
}
