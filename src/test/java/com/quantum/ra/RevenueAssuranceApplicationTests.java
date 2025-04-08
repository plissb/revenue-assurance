package com.quantum.ra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.clickhouse.ClickHouseDataSource;

@SpringBootTest
@Import(TestConfig.class)
class RevenueAssuranceApplicationTests {

    @Autowired
    private ClickHouseDataSource clickHouseDataSource;

    @Test
    void contextLoads() {
        // The test will pass if the application context loads successfully
    }
}
