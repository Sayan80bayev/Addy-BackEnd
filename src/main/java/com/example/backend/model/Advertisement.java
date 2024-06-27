package com.example.backend.model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Double price;
    private LocalDateTime date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;
    private Long views;
    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();
    private transient List<MultipartFile> imageFiles;
<<<<<<< Updated upstream
=======
    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<UserSubscription> subscriptions = new ArrayList<>();
>>>>>>> Stashed changes

    public Advertisement() {
        this.date = LocalDateTime.now();
        this.views = 0L;
    }

    public Advertisement(String title, String description) {
        this.title = title;
        this.description = description;
        this.date = LocalDateTime.now();
    }

    public void setImages(List<MultipartFile> imageFiles) {
        this.imageFiles = imageFiles;

        if (this.images == null) {
            this.images = new ArrayList<>();
        } else {
            this.images.clear();
        }

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                try {
                    Image image = new Image();
                    image.setImageData(file.getBytes()); // Save file content as byte array or stream
                    image.setAdvertisement(this); // Set advertisement reference
                    this.images.add(image);
                } catch (IOException e) {
                    // Handle the exception
                    e.printStackTrace();
                }
            }
        }
    }
}
