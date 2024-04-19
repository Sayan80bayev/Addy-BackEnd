package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.model.Image;
import com.example.backend.repository.ImageRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public Image saveImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setImageData(file.getBytes());
        return imageRepository.save(image);
    }

    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }

    public String validateAndSaveImages(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return null;
            }

            if (!MediaType.IMAGE_JPEG_VALUE.equals(file.getContentType()) &&
                    !MediaType.IMAGE_PNG_VALUE.equals(file.getContentType())) {
                return "Unsupported file type. Only JPEG and PNG images are allowed.";
            }

            long fileSizeInMB = file.getSize() / (1024 * 1024); // Convert bytes to MB
            if (fileSizeInMB > 20) { // Assuming max file size is 5 MB
                return "File size exceeds the maximum limit of 5MB.";
            }
        }
        return null; // Return null to indicate no validation errors
    }

}
