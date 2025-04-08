package com.quantum.ra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import javax.sql.DataSource;

/**
 * Конфигурация подключения к ClickHouse
 */
@Configuration
public class ClickHouseConfig {

    @Value("${clickhouse.url:jdbc:clickhouse://localhost:8123/ra_analytics}")
    private String clickhouseUrl;

    @Value("${clickhouse.username:default}")
    private String clickhouseUsername;

    @Value("${clickhouse.password:}")
    private String clickhousePassword;

    /**
     * Создает бин источника данных ClickHouse
     */
    @Bean(name = "clickHouseDataSource")
    public DataSource clickHouseDataSource() {
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setUser(clickhouseUsername);
        properties.setPassword(clickhousePassword);
        properties.setSocketTimeout(30000);
        properties.setConnectionTimeout(50000);
        
        return new ClickHouseDataSource(clickhouseUrl, properties);
    }
} 