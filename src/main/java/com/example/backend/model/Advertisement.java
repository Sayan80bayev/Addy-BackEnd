package com.example.backend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity
public class Advertisement {
    @Id
    private UUID id = UUID.randomUUID();
    private String title;
    private String description;
    private Double price;
    private LocalDateTime date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;
    private Long views;
    private List<String> image;
    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<UserSubscription> subscriptions;

    public Advertisement() {
        this.date = LocalDateTime.now();
        this.views = 0L;
    }

    public Advertisement(String title, String description) {
        this.title = title;
        this.description = description;
        this.date = LocalDateTime.now();
    }

}
