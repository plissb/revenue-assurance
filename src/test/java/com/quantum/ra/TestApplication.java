package com.quantum.ra;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

/**
 * Тестовое приложение, которое включает только необходимые компоненты для тестов,
 * но не сервисы, требующие внешние зависимости, такие как ClickHouse
 */
@SpringBootApplication
@Import(TestConfig.class)
@ComponentScan(
    basePackages = "com.quantum.ra",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                com.quantum.ra.service.ClickHouseService.class,
                com.quantum.ra.service.FileWatcherService.class
            }
        )
    }
)
@EntityScan("com.quantum.ra.model")
public class TestApplication {
    // Пустой класс для создания тестового контекста Spring Boot
}