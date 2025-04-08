package com.quantum.ra.config;

import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Конфигурация подключения к ClickHouse
 */
@Configuration
@ConditionalOnProperty(name = "clickhouse.enabled", havingValue = "true", matchIfMissing = true)
public class ClickHouseConfig {

    @Value("${clickhouse.url:jdbc:clickhouse://localhost:8124/ra_analytics}")
    private String clickhouseUrl;

    @Value("${clickhouse.username:default}")
    private String clickhouseUsername;

    @Value("${clickhouse.password:}")
    private String clickhousePassword;

    @Value("${clickhouse.database:ra_analytics}")
    private String clickhouseDatabase;

    /**
     * Создает бин DataSource для ClickHouse
     */
    @Bean
    @Lazy
    public ClickHouseDataSource clickHouseDataSource() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", clickhouseUsername);
        properties.setProperty("password", clickhousePassword);
        properties.setProperty("database", clickhouseDatabase);
        
        // Свойства для оптимизации batch-запросов
        properties.setProperty("socket_timeout", "300000");  // 5 минут
        properties.setProperty("connection_timeout", "60000");  // 1 минута
        properties.setProperty("compress", "true");  // Включаем сжатие
        properties.setProperty("decompress", "true");
        properties.setProperty("ssl", "false");
        properties.setProperty("use_server_time_zone", "false");
        properties.setProperty("use_time_zone", "UTC");
        
        return new ClickHouseDataSource(clickhouseUrl, properties);
    }
}