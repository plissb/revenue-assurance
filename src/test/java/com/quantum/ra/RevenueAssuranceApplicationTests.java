package com.quantum.ra;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.yandex.clickhouse.ClickHouseDataSource;

@SpringBootTest
class RevenueAssuranceApplicationTests {

    @MockBean
    private ClickHouseDataSource clickHouseDataSource;

    @Test
    void contextLoads() {
    }

}
