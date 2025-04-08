package com.quantum.ra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель для хранения информации о загруженных файлах
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_uploads")
public class FileUpload {
    
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @Column(nullable = false)
    private String status;
    
    @Column
    private Integer recordsProcessed;
    
    @Column
    private String errorMessage;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
} 