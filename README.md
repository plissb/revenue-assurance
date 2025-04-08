# Revenue Assurance - Docker Setup

## Структура окружения

Окружение состоит из двух баз данных:
- **PostgreSQL** - для хранения операционных данных и метаинформации о загрузках
- **ClickHouse** - для аналитических данных и быстрого доступа к транзакциям

## Запуск окружения

```bash
# Запуск всех сервисов
docker compose up -d

# Просмотр логов сервисов
docker compose logs -f

# Остановка всех сервисов
docker compose down
```

## Доступ к базам данных

### PostgreSQL
- **Хост**: localhost
- **Порт**: 5432
- **База данных**: postgres, ra
- **Пользователь**: postgres
- **Пароль**: postgres

Пример подключения из командной строки:
```bash
psql -h localhost -U postgres -d ra
```

### ClickHouse
- **HTTP интерфейс**: http://localhost:8124
- **TCP порт**: 9000
- **База данных**: ra_analytics
- **Пользователь**: default
- **Пароль**: clickhouse

Пример запроса через HTTP:
```bash
curl 'http://localhost:8124/?user=default&password=clickhouse' --data-binary 'SELECT COUNT() FROM ra_analytics.transactions'
```

## Структура файлов

- `docker-compose.yml` - основной файл конфигурации Docker Compose
- `initdb/init-db.sql` - скрипт инициализации для PostgreSQL
- `clickhouse-init/init.sql` - скрипт инициализации для ClickHouse
- `clickhouse-config/custom.xml` - конфигурация для ClickHouse

## ClickHouse в проекте Spring Boot

В проект добавлены следующие зависимости для работы с ClickHouse:

```groovy
// ClickHouse зависимости
implementation 'com.clickhouse:clickhouse-jdbc:0.6.0-patch5'
implementation 'com.clickhouse:clickhouse-cli-client:0.6.0-patch5'
implementation 'org.apache.httpcomponents:httpclient:4.5.14'
implementation 'org.apache.httpcomponents:httpcore:4.4.16'
implementation 'org.apache.httpcomponents:httpmime:4.5.14'
```

Конфигурация подключения к ClickHouse находится в `application.yaml`:

```yaml
clickhouse:
  url: jdbc:clickhouse://localhost:8124/ra_analytics
  username: default
  password: clickhouse
``` 