package com.quantum.ra.controller;

import com.quantum.ra.model.FileUpload;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST контроллер для загрузки CSV файлов
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class CsvUploadController {

    private final CsvService csvService;

    /**
     * Эндпоинт для загрузки CSV файлов
     * 
     * @param file загружаемый файл
     * @return результат загрузки
     */
    @PostMapping("/csv")
    public ResponseEntity<Map<String, Object>> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        log.info("Получен запрос на загрузку файла: {}, размер: {}", file.getOriginalFilename(), file.getSize());
        
        Map<String, Object> response = new HashMap<>();
        
        // Проверяем, что файл не пустой и имеет расширение CSV
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Файл пуст");
            return ResponseEntity.badRequest().body(response);
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            response.put("success", false);
            response.put("message", "Неверный формат файла. Ожидается CSV");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Сохраняем файл временно для обработки
            String tempFilename = UUID.randomUUID() + "_" + filename;
            Path tempPath = Files.createTempFile("csv_upload_", tempFilename);
            file.transferTo(tempPath);
            
            // Обрабатываем файл
            FileUpload fileUpload = csvService.processCsvFile(tempPath);
            
            // Удаляем временный файл
            Files.deleteIfExists(tempPath);
            
            // Формируем ответ
            response.put("success", "COMPLETED".equals(fileUpload.getStatus()));
            response.put("fileId", fileUpload.getId().toString());
            response.put("status", fileUpload.getStatus());
            response.put("recordsProcessed", fileUpload.getRecordsProcessed());
            
            if ("ERROR".equals(fileUpload.getStatus())) {
                response.put("errorMessage", fileUpload.getErrorMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            log.error("Ошибка при обработке загруженного файла: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Ошибка при обработке файла: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 