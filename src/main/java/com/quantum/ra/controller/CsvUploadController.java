package com.quantum.ra.controller;

import com.quantum.ra.service.CsvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для ручной загрузки CSV файлов
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class CsvUploadController {

    private final CsvService csvService;

    /**
     * Загружает CSV файл и обрабатывает его
     *
     * @param file загруженный файл
     * @return результат обработки
     */
    @PostMapping("/csv")
    public ResponseEntity<Map<String, Object>> uploadCsv(@RequestParam("file") MultipartFile file) {
        log.info("Получен файл для загрузки: {}, размер: {}", file.getOriginalFilename(), file.getSize());
        
        Map<String, Object> response = new HashMap<>();
        
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Файл пуст");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Проверяем расширение файла
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            response.put("success", false);
            response.put("message", "Поддерживаются только файлы CSV");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Сохраняем файл во временную директорию
            Path tempDir = Files.createTempDirectory("csv-upload");
            File tempFile = new File(tempDir.toFile(), fileName);
            
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }
            
            // Обрабатываем файл
            int processedRecords = csvService.processCsvFile(tempFile);
            
            response.put("success", true);
            response.put("fileName", fileName);
            response.put("size", file.getSize());
            response.put("recordsProcessed", processedRecords);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Ошибка при обработке файла: {}", e.getMessage());
            
            response.put("success", false);
            response.put("message", "Ошибка при обработке файла: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 