package com.example.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveFile(MultipartFile file) throws Exception;

    String deleteFile(String fileName);
}