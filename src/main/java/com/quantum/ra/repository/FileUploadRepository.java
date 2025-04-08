package com.quantum.ra.repository;

import com.quantum.ra.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * Находит записи по имени файла
     */
    List<FileUpload> findByFileName(String fileName);
} 