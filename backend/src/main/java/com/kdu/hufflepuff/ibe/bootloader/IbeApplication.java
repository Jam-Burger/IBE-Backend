package com.kdu.hufflepuff.ibe.bootloader;

import com.kdu.hufflepuff.ibe.config.ProjectConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@Import({ProjectConfig.class})
public class IbeApplication {
    public static void main(String[] args) {
        SpringApplication.run(IbeApplication.class, args);
    }
}
