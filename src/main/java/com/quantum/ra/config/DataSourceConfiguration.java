package com.quantum.ra.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    // --- Конфигурация для PostgreSQL (Основной для JPA) ---

    @Bean
    @Primary // Помечаем как основной DataSource
    @ConfigurationProperties("spring.datasource") // Читаем свойства spring.datasource.*
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari") // Доп. настройки HikariCP, если есть
    public DataSource postgresDataSource(DataSourceProperties postgresDataSourceProperties) {
        return postgresDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class) // Используем HikariCP по умолчанию
                .build();
    }

    // --- Конфигурация для ClickHouse ---

    @Bean
    @ConfigurationProperties("clickhouse.datasource") // Читаем свойства clickhouse.datasource.*
    public DataSourceProperties clickhouseDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("clickhouseDataSource") // Даем бину специфичное имя
    @ConfigurationProperties("clickhouse.datasource.hikari")
    public DataSource clickhouseDataSource(@Qualifier("clickhouseDataSourceProperties") DataSourceProperties clickhouseDataSourceProperties) {
        return clickhouseDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("clickhouseJdbcTemplate")
    public JdbcTemplate clickhouseJdbcTemplate(@Qualifier("clickhouseDataSource") DataSource clickhouseDataSource) {
        return new JdbcTemplate(clickhouseDataSource);
    }
}
