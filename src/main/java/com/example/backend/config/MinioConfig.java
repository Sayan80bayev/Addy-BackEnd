package com.example.backend.config;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MinioConfig {

    private static final Logger logger = LoggerFactory.getLogger(MinioConfig.class);

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();

        try {
            // Checking if the connection works by listing buckets (or any other simple API call)
            List<?> buckets = minioClient.listBuckets();
            logger.info("Successfully connected to MinIO at {}", minioUrl);
        } catch (MinioException e) {
            logger.error("Failed to connect to MinIO at {}: {}", minioUrl, e.getMessage());
        } catch (Exception e) {
            logger.error("An error occurred while connecting to MinIO: {}", e.getMessage());
        }

        return minioClient;
    }
}