package com.kdu.hufflepuff.ibe;

import com.kdu.hufflepuff.ibe.bootloader.IbeApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = IbeApplication.class)
class IbeApplicationTests {
    @Test
    void contextLoads() {
        log.debug("Starting tests...");
    }
}