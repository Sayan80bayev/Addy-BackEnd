package com.example.backend.service;

import com.example.backend.service.impl.FileServiceImpl;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fileService, "bucketName", "addy");
    }

    @Test
    void testSaveFileSuccess() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        String fileName = "test.txt";
        String contentType = "text/plain";

        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getSize()).thenReturn(123L);
        when(file.getContentType()).thenReturn(contentType);
        // Act
        String fileUrl = fileService.saveFile(file);

        // Assert
        verify(minioClient).putObject(any(PutObjectArgs.class));
        assertEquals("null/addy/test.txt", fileUrl);
    }

    @Test
    void testSaveFileEmptyFile() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("File is empty");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> fileService.saveFile(file));
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void testDeleteFileSuccess() throws Exception {
        // Arrange
        String fileName = "test.txt";
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("File deleted successfully");

        // Act
        String result = fileService.deleteFile(fileName);

        // Assert
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
        assertEquals("File deleted successfully", result);
    }

    @Test
    void testDeleteFileIOException() throws Exception {
        String fileName = "test.txt";
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("java.io.IOException");

        doThrow(IOException.class).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        Exception exception = assertThrows(RuntimeException.class, () -> fileService.deleteFile(fileName));
        assertTrue(exception.getMessage().contains("java.io.IOException"));
    }

    @Test
    void testDeleteFileInvalidKeyException() throws Exception {
        // Arrange
        String fileName = "test.txt";
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Invalid key exception");

        doThrow(InvalidKeyException.class).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> fileService.deleteFile(fileName));
        assertTrue(exception.getMessage().contains("Invalid key exception"));
    }

    @Test
    void testDeleteFileNoSuchAlgorithmException() throws Exception {
        // Arrange
        String fileName = "test.txt";
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("No such algorithm exception");

        doThrow(NoSuchAlgorithmException.class).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> fileService.deleteFile(fileName));
        assertTrue(exception.getMessage().contains("No such algorithm exception"));
    }
}