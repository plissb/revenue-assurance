package com.quantum.ra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickHouseService {

    @Qualifier("clickHouseDataSource")
    private final DataSource clickHouseDataSource;

    /**
     * Сохраняет транзакции в ClickHouse
     * 
     * @param transactions список транзакций для сохранения
     * @param fileName имя исходного файла
     * @return количество сохраненных записей
     */
    public int saveTransactions(List<Object> transactions, String fileName) {
        String sql = "INSERT INTO ra_analytics.transactions " +
                "(id, sender_msisdn, receiver_msisdn, receiver_user_id, sender_user_id, " +
                "transaction_amount, commissions_paid, commissions_received, commissions_others, " +
                "service_charge_received, service_charge_paid, taxes, service_type, transfer_status, " +
                "sender_pre_bal, sender_post_bal, receiver_pre_bal, receiver_post_bal, " +
                "sender_acc_status, receiver_acc_status, error_code, error_desc, reference_number, " +
                // ... остальные поля
                "file_name, load_timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                // ... значения для остальных полей
                "?, ?)";

        try (Connection connection = clickHouseDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int batchSize = 0;
            for (Object transaction : transactions) {
                // TODO: Заполнение полей PreparedStatement на основе объекта transaction
                // Пример:
                // statement.setObject(1, UUID.randomUUID());
                // statement.setString(2, transaction.getSenderMsisdn());
                // ...
                
                // Добавляем метаданные загрузки
                statement.setString(24, fileName);
                statement.setObject(25, LocalDateTime.now());
                
                statement.addBatch();
                batchSize++;
                
                // Выполняем пакетную вставку каждые 1000 записей
                if (batchSize % 1000 == 0) {
                    statement.executeBatch();
                }
            }
            
            // Выполняем оставшиеся записи
            if (batchSize % 1000 != 0) {
                statement.executeBatch();
            }
            
            return batchSize;
        } catch (SQLException e) {
            log.error("Ошибка при сохранении данных в ClickHouse", e);
            throw new RuntimeException("Ошибка при сохранении данных в ClickHouse", e);
        }
    }
} 