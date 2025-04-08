package com.quantum.ra.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Сервис для отслеживания новых файлов в директории input
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileWatcherService {

    private final CsvService csvService;

    @Value("${app.file-watcher.input-dir:data/input}")
    private String inputDirectoryPath;

    @Value("${app.file-watcher.file-pattern:*.csv}")
    private String filePattern;

    @Value("${app.file-watcher.enabled:true}")
    private boolean enabled;

    /**
     * Запускается каждую секунду для проверки новых файлов
     */
    @Scheduled(fixedDelayString = "${app.file-watcher.interval:1000}")
    public void checkNewFiles() {
        if (!enabled) {
            return;
        }

        log.debug("Проверка новых файлов в директории {}", inputDirectoryPath);

        Path inputDir = Paths.get(inputDirectoryPath);
        File directory = inputDir.toFile();

        if (!directory.exists() || !directory.isDirectory()) {
            log.error("Директория {} не существует или не является директорией", inputDirectoryPath);
            return;
        }

        // Фильтр для поиска файлов соответствующих маске
        FileFilter fileFilter = WildcardFileFilter.builder()
                .setWildcards(filePattern)
                .setIoCase(IOCase.INSENSITIVE).get();
        File[] files = directory.listFiles(fileFilter);

        if (files == null || files.length == 0) {
            log.debug("Новые файлы не найдены");
            return;
        }

        // Сортируем файлы по времени изменения (сначала старые)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            log.info("Обнаружен новый файл для обработки: {}", file.getName());
            try {
                csvService.processCsvFile(file);
            } catch (Exception e) {
                log.error("Ошибка при обработке файла {}: {}", file.getName(), e.getMessage());
            }
        }
    }
} 
