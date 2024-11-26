package com.example.backend.service;

import com.example.backend.dto.request.AdvertisementRequest;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.mapper.AdvertisementMapper;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.model.User;
import com.example.backend.repository.AdvertisementRepository;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.service.impl.AdvertisementServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@Slf4j
public class AdvertisementServiceImplTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FileService fileService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private AdvertisementMapper advertisementMapper;

    @InjectMocks
    private AdvertisementServiceImpl advertisementService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the SecurityContext and Authentication
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Mock the principal (user details) in Authentication
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        when(authentication.getPrincipal()).thenReturn(mockUser);
    }

    @Test
    public void createAdvertisement_Success() {
        // Arrange
        AdvertisementRequest advertisementRequest = new AdvertisementRequest();
        advertisementRequest.setCategoryId(UUID.randomUUID());
        List<MultipartFile> images = Collections.emptyList();

        Category mockCategory = new Category();
        mockCategory.setId(advertisementRequest.getCategoryId());

        Advertisement mockAdvertisement = new Advertisement();
        mockAdvertisement.setId(UUID.randomUUID());
        mockAdvertisement.setCategory(mockCategory);

        when(categoryRepository.findById(advertisementRequest.getCategoryId())).thenReturn(Optional.of(mockCategory));
        when(advertisementMapper.toEntity(advertisementRequest)).thenReturn(mockAdvertisement);
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(mockAdvertisement);
        when(advertisementMapper.toResponse(mockAdvertisement)).thenReturn(new AdvertisementResponse());

        // Act
        AdvertisementResponse response = advertisementService.createAdvertisement(advertisementRequest, images);

        // Assert
        verify(advertisementRepository).save(any(Advertisement.class));
        assertEquals(mockAdvertisement.getCategory(), mockCategory);
        log.info("Advertisement creation test passed.");
    }

    @Test
    public void findById_Success() {
        // Arrange
        UUID advertisementId = UUID.randomUUID();
        Advertisement mockAdvertisement = new Advertisement();
        mockAdvertisement.setId(advertisementId);

        when(advertisementRepository.findById(advertisementId)).thenReturn(Optional.of(mockAdvertisement));
        when(advertisementMapper.toResponse(mockAdvertisement)).thenReturn(new AdvertisementResponse());

        // Act
        AdvertisementResponse response = advertisementService.findById(advertisementId);

        // Assert
        assertNotNull(response);
        assertEquals(mockAdvertisement.getId(), response.getId());
        verify(advertisementRepository).findById(advertisementId);
        log.info("Find by ID test passed.");
    }

    @Test
    public void findById_NotFound() {
        // Arrange
        UUID advertisementId = UUID.randomUUID();
        when(advertisementRepository.findById(advertisementId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            advertisementService.findById(advertisementId);
        });
        assertNotNull(exception);
        verify(advertisementRepository).findById(advertisementId);
    }

    @Test
    public void updateAdvertisement_Success() {
        // Arrange
        UUID advertisementId = UUID.randomUUID();
        AdvertisementRequest advertisementRequest = new AdvertisementRequest();
        advertisementRequest.setCategoryId(UUID.randomUUID());
        List<MultipartFile> images = Collections.emptyList();

        // Create a mock user
        User mockUser = new User();
        mockUser.setEmail("test@example.com"); // Set the same email that will be used for authentication

        // Create a mock category
        Category mockCategory = new Category();
        mockCategory.setId(advertisementRequest.getCategoryId());

        // Create an existing advertisement and set the user
        Advertisement existingAdvertisement = new Advertisement();
        existingAdvertisement.setId(advertisementId);
        existingAdvertisement.setUser(mockUser); // Set the same user

        // Mock the behavior
        when(advertisementRepository.findById(advertisementId)).thenReturn(Optional.of(existingAdvertisement));
        when(advertisementMapper.toEntity(advertisementRequest)).thenReturn(new Advertisement());
        when(categoryRepository.findById(advertisementRequest.getCategoryId())).thenReturn(Optional.of(mockCategory));
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(existingAdvertisement);
        when(advertisementMapper.toResponse(existingAdvertisement)).thenReturn(new AdvertisementResponse());

        // Mock the authenticated user to match the existing advertisement's user
        when(authentication.getPrincipal()).thenReturn(mockUser);

        // Act
        AdvertisementResponse response = advertisementService.updateAdvertisement(advertisementRequest, images, advertisementId);

        // Assert
        verify(advertisementRepository).save(any(Advertisement.class));
        assertNotNull(response);
    }

    @Test
    public void updateAdvertisement_NotFound() {
        // Arrange
        UUID advertisementId = UUID.randomUUID();
        AdvertisementRequest advertisementRequest = new AdvertisementRequest();
        advertisementRequest.setCategoryId(UUID.randomUUID());

        when(advertisementRepository.findById(advertisementId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            advertisementService.updateAdvertisement(advertisementRequest, Collections.emptyList(), advertisementId);
        });
        assertNotNull(exception);
        verify(advertisementRepository).findById(advertisementId);
    }

    @Test
    public void deleteAdvertisement_Success() {
        // Arrange
        UUID advertisementId = UUID.randomUUID();
        Advertisement mockAdvertisement = new Advertisement();
        mockAdvertisement.setId(advertisementId);
        mockAdvertisement.setTitle("Test Ad");
        mockAdvertisement.setSubscriptions(Collections.emptyList());

        when(advertisementRepository.findById(advertisementId)).thenReturn(Optional.of(mockAdvertisement));

        // Act
        advertisementService.deleteById(advertisementId);

        // Assert
        verify(advertisementRepository).deleteById(advertisementId);
    }

    @Test
    public void deleteAdvertisement_NotFound() {
        // Arrange
        UUID advertisementId = UUID.randomUUID();
        when(advertisementRepository.findById(advertisementId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            advertisementService.deleteById(advertisementId);
        });
        assertNotNull(exception);
        verify(advertisementRepository).findById(advertisementId);
    }

    @Test
    public void findAll_Success() {
        // Arrange
        Advertisement mockAdvertisement = new Advertisement();
        when(advertisementRepository.findAll()).thenReturn(Collections.singletonList(mockAdvertisement));
        when(advertisementMapper.toResponse(mockAdvertisement)).thenReturn(new AdvertisementResponse());

        // Act
        List<AdvertisementResponse> responses = advertisementService.findAll();

        // Assert
        assertEquals(1, responses.size());
        verify(advertisementRepository).findAll();
    }

    // Add more tests for other methods if needed
}