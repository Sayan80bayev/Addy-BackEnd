package com.example.backend.service.impl;

import com.example.backend.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileServiceImpl implements FileService {

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    private final MinioClient minioClient;
    private final MessageSource messageSource;

    public FileServiceImpl(MinioClient minioClient, MessageSource messageSource) {
        this.minioClient = minioClient;
        this.messageSource = messageSource;
    }

    @Override
    public String saveFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("file.empty.upload", null, null));
        }

        String fileName = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            // Загрузка файла в MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException(
                    messageSource.getMessage("file.upload.failed", new Object[]{fileName}, null), e);
        }

        // Возвращаем прямую ссылку для доступа к файлу
        return String.format("%s/%s/%s", minioUrl, bucketName, fileName);
    }

    @Override
    public String deleteFile(String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException(
                    messageSource.getMessage("file.delete.failed", new Object[]{fileName}, null), e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return messageSource.getMessage("file.delete.success", new Object[]{fileName}, null);
    }
}