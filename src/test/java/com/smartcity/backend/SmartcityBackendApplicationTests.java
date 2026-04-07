package com.smartcity.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SmartcityBackendApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("✅ Backend Loaded Successfully");
    }
}