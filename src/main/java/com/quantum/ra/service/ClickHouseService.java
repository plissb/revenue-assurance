package com.quantum.ra.service;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseFormat;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseRequest;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseDataStreamFactory;
import com.clickhouse.data.ClickHouseOutputStream;
import com.clickhouse.data.ClickHouseWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickHouseService {

    private final ClickHouseClient clickHouseClient;
    private final ClickHouseNode clickHouseNode;

    /**
     * Сохраняет транзакции в ClickHouse используя бинарный формат
     *
     * @param transactions список транзакций для сохранения
     * @param fileName имя исходного файла
     * @return количество сохраненных записей
     */
    public int saveTransactions(List<Object> transactions, String fileName) {
        String sql = "INSERT INTO ra_analytics.transactions FORMAT Binary";

        try (ClickHouseClient client = clickHouseClient) {
            int[] count = {0};  // Используем массив, чтобы изменять в лямбде
            
            ClickHouseResponse response = client.connect(clickHouseNode)
                    .format(ClickHouseFormat.Binary)
                    .query(sql)
                    .execute()
                    .thenApply(resp -> {
                        try (ClickHouseWriter writer = resp.getWriter()) {
                            for (Object transaction : transactions) {
                                // Здесь будет заполнение данных конкретного объекта
                                // Необходимо адаптировать под ваш класс транзакции
                                UUID id = UUID.randomUUID();
                                writer.writeUuid(id);
                                
                                // Пример заполнения строковых полей (закомментировано, т.к. нет класса Transaction)
                                // writer.writeString(transaction.getSenderMsisdn());
                                // writer.writeString(transaction.getReceiverMsisdn());
                                
                                // ... Все остальные поля таблицы
                                
                                // Метаданные
                                writer.writeString(fileName);
                                writer.writeDateTime64(LocalDateTime.now(), 3);
                                
                                count[0]++;
                            }
                            return resp;
                        } catch (IOException e) {
                            log.error("Error writing to ClickHouse: ", e);
                            throw new RuntimeException("Error writing to ClickHouse", e);
                        }
                    }).get();
            
            long rowsWritten = response.getSummary().getWrittenRows();
            log.info("Successfully loaded {} rows into ClickHouse", rowsWritten);
            return count[0];

        } catch (ExecutionException | InterruptedException e) {
            log.error("Error saving data to ClickHouse: ", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error saving data to ClickHouse", e);
        }
    }
} 