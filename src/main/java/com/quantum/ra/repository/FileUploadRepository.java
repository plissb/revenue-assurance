package com.quantum.ra.repository;

import com.quantum.ra.model.FileUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с записями о загруженных файлах
 */
@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, UUID> {
    
    /**
     * Находит записи по статусу
     */
    List<FileUpload> findByStatus(String status);
    
    /**
     * Находит записи по статусу с пагинацией
     */
    Page<FileUpload> findByStatus(String status, Pageable pageable);
    
    /**
     * Находит записи по имени файла
     */
    List<FileUpload> findByFileName(String fileName);
    
    /**
     * Находит записи по части имени файла (без учета регистра)
     */
    Page<FileUpload> findByFileNameContainingIgnoreCase(String fileName, Pageable pageable);
    
    /**
     * Находит записи в указанном диапазоне дат
     */
    Page<FileUpload> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Получает список всех уникальных статусов
     */
    @Query("SELECT DISTINCT f.status FROM FileUpload f")
    List<String> findDistinctStatuses();
} 