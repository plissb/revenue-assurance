package com.quantum.ra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import javax.sql.DataSource;

/**
 * Конфигурация подключения к ClickHouse
 */
@Configuration
@ConditionalOnProperty(name = "clickhouse.enabled", havingValue = "true", matchIfMissing = true)
public class ClickHouseConfig {

    @Value("${clickhouse.url:jdbc:clickhouse://localhost:8123/ra_analytics}")
    private String clickhouseUrl;

    @Value("${clickhouse.username:default}")
    private String clickhouseUsername;

    @Value("${clickhouse.password:}")
    private String clickhousePassword;

    /**
     * Создает бин источника данных ClickHouse с отложенной инициализацией
     */
    @Bean(name = "clickHouseDataSource")
    @Lazy
    public DataSource clickHouseDataSource() {
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setUser(clickhouseUsername);
        properties.setPassword(clickhousePassword);
        properties.setSocketTimeout(30000);
        properties.setConnectionTimeout(50000);
        
        return new ClickHouseDataSource(clickhouseUrl, properties);
    }
} 