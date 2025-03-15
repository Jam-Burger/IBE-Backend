package com.kdu.hufflepuff.ibe.bootloader;

import com.kdu.hufflepuff.ibe.config.ProjectConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({ProjectConfig.class})
@SpringBootApplication
public class IbeApplication {
    public static void main(String[] args) {
        SpringApplication.run(IbeApplication.class, args);
    }
}
