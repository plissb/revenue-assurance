package com.quantum.ra;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
class RevenueAssuranceApplicationTests {

    @Test
    void contextLoads() {
        // The test will pass if the application context loads successfully
    }
}
