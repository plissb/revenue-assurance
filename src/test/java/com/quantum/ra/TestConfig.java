package com.quantum.ra;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.clickhouse.ClickHouseDataSource;

/**
 * Configuration class for tests that provides mock beans
 */
@TestConfiguration
public class TestConfig {

    @Bean
    public ClickHouseDataSource clickHouseDataSource() {
        return Mockito.mock(ClickHouseDataSource.class);
    }
} 