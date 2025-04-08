package com.quantum.ra.service;

import com.quantum.ra.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для обработки CSV файлов с транзакциями
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CsvService {

    private final ClickHouseService clickHouseService;

    /**
     * Загружает данные из CSV файла в ClickHouse
     *
     * @param file файл CSV для загрузки
     * @return количество загруженных записей
     */
    public int processCsvFile(File file) {
        log.info("Начало обработки файла: {}", file.getName());
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Пропускаем заголовок
            String headerLine = reader.readLine();
            if (headerLine == null) {
                log.error("Файл {} пуст", file.getName());
                return 0;
            }
            
            log.info("Заголовок CSV: {}", headerLine);
            
            // Читаем данные
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    Transaction transaction = Transaction.fromCsvLine(line, file.getName());
                    transactions.add(transaction);
                } catch (Exception e) {
                    log.error("Ошибка в строке {}: {}", lineNumber, e.getMessage());
                }
            }
            
            log.info("Прочитано {} транзакций из файла {}", transactions.size(), file.getName());
            
            // Загружаем данные в ClickHouse
            if (!transactions.isEmpty()) {
                return clickHouseService.saveTransactions(transactions, file.getName());
            }
            
        } catch (IOException e) {
            log.error("Ошибка при чтении файла {}: {}", file.getName(), e.getMessage());
        }
        
        return 0;
    }

    /**
     * Загружает данные из CSV файла в ClickHouse
     *
     * @param path путь к файлу CSV
     * @return количество загруженных записей
     */
    public int processCsvFile(Path path) {
        return processCsvFile(path.toFile());
    }
} 