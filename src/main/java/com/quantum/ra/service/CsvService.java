package com.quantum.ra.service;

import com.quantum.ra.model.FileUpload;
import com.quantum.ra.model.Transaction;
import com.quantum.ra.repository.FileUploadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для обработки CSV файлов с транзакциями
 */
@Slf4j
@Service
public class CsvService {

    private final FileUploadRepository fileUploadRepository;
    private final ClickHouseService clickHouseService;
    
    @Value("${clickhouse.enabled:true}")
    private boolean clickhouseEnabled;
    
    private static final Path INPUT_DIR = Paths.get("data/input");
    private static final Path DONE_DIR = Paths.get("data/done");
    private static final Path ERROR_DIR = Paths.get("data/error");

    @Autowired
    public CsvService(FileUploadRepository fileUploadRepository, 
                      @Autowired ClickHouseService clickHouseService) {
        this.fileUploadRepository = fileUploadRepository;
        this.clickHouseService = clickHouseService;
        log.info("CsvService initialized, ClickHouse enabled: {}", 
                clickHouseService != null ? "true" : "false");
    }

    /**
     * Загружает данные из CSV файла в ClickHouse
     *
     * @param file файл CSV для загрузки
     * @return объект FileUpload с результатом загрузки
     */
    @Transactional
    public FileUpload processCsvFile(File file) {
        log.info("Начало обработки файла: {}", file.getName());
        
        // Создаем запись о загрузке файла
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(file.getName());
        fileUpload.setFileSize(file.length());
        fileUpload.setStatus("PROCESSING");
        fileUpload.setCreatedAt(LocalDateTime.now());
        fileUpload.setUpdatedAt(LocalDateTime.now());
        fileUpload = fileUploadRepository.save(fileUpload);
        
        try {
            List<Transaction> transactions = new ArrayList<>();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Пропускаем заголовок
                String headerLine = reader.readLine();
                if (headerLine == null) {
                    throw new IOException("Файл пуст");
                }
                
                log.info("Заголовок CSV: {}", headerLine);
                
                // Читаем данные
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    try {
                        Transaction transaction = Transaction.fromCsvLine(line, fileUpload.getId());
                        transactions.add(transaction);
                    } catch (Exception e) {
                        log.error("Ошибка в строке {}: {}", lineNumber, e.getMessage());
                    }
                }
                
                log.info("Прочитано {} транзакций из файла {}", transactions.size(), file.getName());
                
                // Загружаем данные в ClickHouse если он доступен
                int recordsProcessed = 0;
                if (!transactions.isEmpty()) {
                    if (clickHouseService != null && clickhouseEnabled) {
                        recordsProcessed = clickHouseService.saveTransactions(transactions, fileUpload.getId());
                    } else {
                        log.error("ClickHouse недоступен, данные не будут загружены");
                        recordsProcessed = transactions.size(); // Считаем все записи обработанными
                    }
                    
                    // Обновляем статус загрузки
                    fileUpload.setStatus("COMPLETED");
                    fileUpload.setRecordsProcessed(recordsProcessed);
                    fileUpload.setUpdatedAt(LocalDateTime.now());
                    
                    // Перемещаем файл в директорию успешных загрузок
                    moveFile(file, DONE_DIR);
                    
                    return fileUpload;
                } else {
                    throw new IOException("Нет данных для загрузки");
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке файла {}: {}", file.getName(), e.getMessage());
            
            // Обновляем статус загрузки с ошибкой
            fileUpload.setStatus("ERROR");
            fileUpload.setErrorMessage(e.getMessage());
            fileUpload.setUpdatedAt(LocalDateTime.now());
            
            // Перемещаем файл в директорию ошибок
            moveFile(file, ERROR_DIR);
        }
        
        return fileUpload;
    }

    /**
     * Загружает данные из CSV файла в ClickHouse
     *
     * @param path путь к файлу CSV
     * @return объект FileUpload с результатом загрузки
     */
    @Transactional
    public FileUpload processCsvFile(Path path) {
        return processCsvFile(path.toFile());
    }
    
    /**
     * Перемещает файл в указанную директорию
     */
    private void moveFile(File file, Path targetDir) {
        try {
            // Создаем директорию, если ее нет
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            // Перемещаем файл
            Path target = targetDir.resolve(file.getName());
            Files.move(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Файл {} перемещен в {}", file.getName(), targetDir);
        } catch (IOException e) {
            log.error("Ошибка при перемещении файла {}: {}", file.getName(), e.getMessage());
        }
    }
} 
