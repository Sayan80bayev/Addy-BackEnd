package com.example.backend.service;

import com.example.backend.dto.request.SubscribeRequest;
import com.example.backend.mapper.UserSubscriptionMapper;
import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.AdvertisementRepository;
import com.example.backend.repository.SubscribtionRepository;
import com.example.backend.service.impl.SubscriptionServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.text.html.parser.Entity;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SubscriptionServiceImplTest {

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private SubscribtionRepository subscribtionRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private final UserSubscriptionMapper mapper = Mappers.getMapper(UserSubscriptionMapper.class);

    @Mock
    private MessageSource messageSource;

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
    public void subscribeUser_Success(){
        // Arrange
        UUID adId = UUID.randomUUID(); // Use a consistent ID for Advertisement
        SubscribeRequest request = SubscribeRequest.builder()
                .id(adId)
                .email("test@example.com")
                .build();

        Advertisement advertisement = Advertisement.builder().id(adId).build();
        when(advertisementRepository.findById(request.getId()))
                .thenReturn(Optional.of(advertisement));

        // Act
        subscriptionService.subscribeUser(request); // Call the method to test

        // Capture the argument passed to save
        ArgumentCaptor<UserSubscription> captor = ArgumentCaptor.forClass(UserSubscription.class);
        verify(subscribtionRepository, times(1)).save(captor.capture()); // Now this should work

        // Assert
        UserSubscription userSubscription = captor.getValue(); // Now this will have the saved UserSubscription

        verify(advertisementRepository, times(1)).findById(request.getId());
        Assert.assertEquals("test@example.com", userSubscription.getUser().getEmail()); // Check if the email matches
        Assert.assertEquals(advertisement, userSubscription.getAd());
    }
    @Test
    public void subscribeUser_Error(){
        UUID adId = UUID.randomUUID();
        SubscribeRequest request = SubscribeRequest.builder()
                .id(adId)
                .email("test@example.com")
                .build();

        when(advertisementRepository.findById(adId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                "advertisement.not.found",
                new Object[]{adId.toString()},
                LocaleContextHolder.getLocale()))
                .thenReturn("Advertisement not found");

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.subscribeUser(request);
        });

        assertEquals("Advertisement not found", exception.getMessage());

        verify(advertisementRepository, times(1)).findById(adId);
        verify(subscribtionRepository, never()).save(any());
    }


}