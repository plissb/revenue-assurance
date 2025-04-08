package com.quantum.ra.controller;

import com.quantum.ra.model.FileUpload;
import com.quantum.ra.repository.FileUploadRepository;
import com.quantum.ra.service.CsvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Контроллер для веб-интерфейса работы с файлами
 */
@Slf4j
@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileViewController {

    private final FileUploadRepository fileUploadRepository;
    private final CsvService csvService;
    
    /**
     * Отображает список загруженных файлов с возможностью поиска и фильтрации
     */
    @GetMapping
    public String listFiles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            Model model) {
        
        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
        
        Page<FileUpload> filesPage;
        
        // Применяем фильтры, если они указаны
        if (search != null && !search.isEmpty()) {
            filesPage = fileUploadRepository.findByFileNameContainingIgnoreCase(search, pageable);
        } else if (status != null && !status.isEmpty()) {
            filesPage = fileUploadRepository.findByStatus(status, pageable);
        } else if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            try {
                // Преобразование строки в LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                LocalDateTime end = LocalDateTime.parse(endDate, formatter);
                filesPage = fileUploadRepository.findByCreatedAtBetween(start, end, pageable);
            } catch (DateTimeParseException e) {
                log.error("Ошибка парсинга даты: {}", e.getMessage());
                filesPage = fileUploadRepository.findAll(pageable);
            }
        } else {
            filesPage = fileUploadRepository.findAll(pageable);
        }
        
        model.addAttribute("filesPage", filesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", filesPage.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        // Список возможных статусов для фильтра
        List<String> statuses = fileUploadRepository.findDistinctStatuses();
        model.addAttribute("statuses", statuses);
        
        return "file-list";
    }
    
    /**
     * Отображает форму загрузки файла
     */
    @GetMapping("/upload")
    public String showUploadForm() {
        return "file-upload";
    }
    
    /**
     * Обрабатывает загрузку файла
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, 
                                   RedirectAttributes redirectAttributes) {
        log.info("Получен запрос на загрузку файла через веб-интерфейс: {}, размер: {}", 
                file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Пожалуйста, выберите файл для загрузки");
            return "redirect:/files/upload";
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("error", "Неверный формат файла. Ожидается CSV");
            return "redirect:/files/upload";
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
            
            if ("ERROR".equals(fileUpload.getStatus())) {
                redirectAttributes.addFlashAttribute("error", 
                        "Ошибка при обработке файла: " + fileUpload.getErrorMessage());
            } else {
                redirectAttributes.addFlashAttribute("success", 
                        "Файл успешно загружен. Обработано записей: " + fileUpload.getRecordsProcessed());
            }
            
            return "redirect:/files";
            
        } catch (IOException e) {
            log.error("Ошибка при обработке загруженного файла: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Ошибка при обработке файла: " + e.getMessage());
            return "redirect:/files/upload";
        }
    }
    
    /**
     * Отображает детальную информацию о файле
     */
    @GetMapping("/{id}")
    public String viewFileDetails(@PathVariable UUID id, Model model) {
        Optional<FileUpload> fileUploadOpt = fileUploadRepository.findById(id);
        
        if (fileUploadOpt.isEmpty()) {
            return "redirect:/files";
        }
        
        model.addAttribute("file", fileUploadOpt.get());
        return "file-details";
    }
}