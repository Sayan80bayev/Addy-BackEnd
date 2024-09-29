package com.example.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.model.UserSubscription;

import jakarta.transaction.Transactional;

public interface SubscribtionRepository extends JpaRepository<UserSubscription, UUID> {
    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :id")
    List<UserSubscription> getSubcribtionsByUserID(UUID id);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSubscription s WHERE s.user.id = :user_id AND s.ad.id = :id")
    void deleteSubs(@Param("user_id") UUID userId, @Param("id") UUID adId);

}
