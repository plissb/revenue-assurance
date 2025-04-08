package com.quantum.ra.config;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseCredentials;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseProtocol;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ClickHouseConfig {

    @Value("${clickhouse.host:localhost}")
    private String host;

    @Value("${clickhouse.port:8124}")
    private int port;

    @Value("${clickhouse.database:ra_analytics}")
    private String database;

    @Value("${clickhouse.username:default}")
    private String username;

    @Value("${clickhouse.password:clickhouse}")
    private String password;

    @Bean(destroyMethod = "close")
    public ClickHouseClient clickHouseClient() {
        return ClickHouseClient.newInstance();
    }

    @Bean
    public ClickHouseNode clickHouseNode() {
        return ClickHouseNode.builder()
                .host(host)
                .port(ClickHouseProtocol.HTTP, port)
                .database(database)
                .credentials(ClickHouseCredentials.fromUserAndPassword(username, password))
                .build();
    }
} 