package com.example.backend.service.impl;

import com.example.backend.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

@Service
public class FileServiceImpl implements FileService {

    private final String bucketName = System.getProperty("TIGRIS_BUCKET_NAME");

    private final String s3Url = System.getProperty("TIGRIS_URL");

    private final String accessKey= System.getProperty("TIGRIS_ACCESS_KEY");

    private final String secretKey = System.getProperty("TIGRIS_SECRET_KEY");

    private final S3Client s3Client;
    private final MessageSource messageSource;

    public FileServiceImpl(MessageSource messageSource) {
        this.messageSource = messageSource;

        // Create S3 client using AWS SDK v2
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(s3Url))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of("auto")) // Set your preferred region
                .build();
    }

    @Override
    public String saveFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(messageSource.getMessage("file.empty.upload", null, null));
        }

        String fileName = file.getOriginalFilename();
        File tempFile = File.createTempFile("upload-", fileName);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(messageSource.getMessage("file.upload.failed", new Object[]{fileName}, null), e);
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, tempFile.toPath());
        } catch (S3Exception e) {
            throw new RuntimeException(messageSource.getMessage("file.upload.failed", new Object[]{fileName}, null), e);
        } finally {
            Files.deleteIfExists(tempFile.toPath());
        }

        return String.format("%s/%s/%s", s3Url, bucketName, fileName);
    }

    @Override
    public String deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException(messageSource.getMessage("file.delete.failed", new Object[]{fileName}, null), e);
        }
        return messageSource.getMessage("file.delete.success", new Object[]{fileName}, null);
    }
}