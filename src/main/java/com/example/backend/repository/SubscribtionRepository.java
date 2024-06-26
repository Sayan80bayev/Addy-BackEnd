package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.model.UserSubscription;

import jakarta.transaction.Transactional;

public interface SubscribtionRepository extends JpaRepository<UserSubscription, Long> {
    @Query("SELECT s FROM UserSubscription s WHERE s.user.id = :id")
    List<UserSubscription> getSubcribtionsByUserID(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSubscription s WHERE s.user.id = :user_id AND s.ad.id = :id")
    void deleteSubs(@Param("user_id") Long userId, @Param("id") Long adId);

}
