package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    public String saveFile(MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file.");
        }

        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory, fileName);

        Files.createDirectories(filePath.getParent());

        file.transferTo(filePath.toFile());

        return uploadDirectory + fileName;
    }

    public String deleteFile(String fileName) {
        Path filePath = Paths.get(uploadDirectory, fileName);
        File file = filePath.toFile();

        if (file.exists()) {
            if (file.delete()) {
                return "File deleted successfully: " + fileName;
            } else {
                throw new RuntimeException("Failed to delete file: " + fileName);
            }
        } else {
            throw new IllegalArgumentException("File not found: " + fileName);
        }
    }
}
