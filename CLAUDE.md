# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands
- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- Run all tests: `./gradlew test`
- Run single test: `./gradlew test --tests com.quantum.ra.package.ClassName`
- Docker environment: `docker-compose up -d`

## Code Style Guidelines
- Java 21 with Spring Boot 3.4.x
- 4-space indentation, 120 char line limit
- Lombok annotations used to reduce boilerplate
- Naming: camelCase (variables/methods), PascalCase (classes), UPPER_SNAKE (constants)
- Error handling: use specific exceptions with descriptive messages
- Logging: SLF4J with @Slf4j (Lombok)
- Use BigDecimal for financial values, not float/double
- Constructor injection for dependencies
- Group imports logically: Spring, Java standard, project-specific
- Always use try-with-resources for closeable resources
- Document with Javadoc (can be in Russian)