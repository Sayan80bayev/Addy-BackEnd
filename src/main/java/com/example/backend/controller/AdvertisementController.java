package com.example.backend.controller;

import com.example.backend.dto.request.AdvertisementRequest;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.impl.AdvertisementServiceImpl;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/v1/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {
        private final AdvertisementService service;

        @Operation(summary = "Save a new advertisement", description = "Creates a new advertisement with the provided data and images.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Advertisement successfully saved"),
                        @ApiResponse(responseCode = "400", description = "Invalid request"),
                        @ApiResponse(responseCode = "500", description = "Server error")
        })
        @PostMapping(consumes = "multipart/form-data")
        public ResponseEntity<?> saveAdvertisement(@RequestPart("advertisement") AdvertisementRequest aDto,
                        @RequestPart(value = "files", required = false) List<MultipartFile> files) {
                return ResponseEntity.ok(service.createAdvertisement(aDto, files));
        }

        @Operation(summary = "Delete an advertisement", description = "Deletes an advertisement by the specified ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Advertisement successfully deleted"),
                        @ApiResponse(responseCode = "404", description = "Advertisement not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteAdvertisement(@PathVariable("id") UUID id) {
                service.deleteById(id);
                return ResponseEntity.ok("__SUCCESS__");
        }

        @Operation(summary = "Edit an advertisement", description = "Updates the data of the advertisement with the specified ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Advertisement successfully updated"),
                        @ApiResponse(responseCode = "404", description = "Advertisement not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request")
        })
        @PutMapping("/{id}")
        public ResponseEntity<?> editAdvertisement(@PathVariable("id") UUID id,
                        @RequestPart("advertisement") AdvertisementRequest aDto,
                        @RequestPart(value = "files", required = false) List<MultipartFile> files) {
                return ResponseEntity.ok(service.updateAdvertisement(aDto, files, id));
        }

        @Operation(summary = "Get all advertisements", description = "Returns a list of all advertisements.")
        @ApiResponse(responseCode = "200", description = "List of advertisements successfully retrieved")
        @GetMapping("/getAll")
        public List<AdvertisementResponse> getAdds() {
                return service.findAll();
        }

        @Operation(summary = "Search advertisement by name", description = "Returns a list of advertisements matching the specified name.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Advertisements successfully found"),
                        @ApiResponse(responseCode = "404", description = "Advertisements not found")
        })
        @GetMapping("/search/{name}")
        public List<AdvertisementResponse> search(@PathVariable("name") String name) {
                return service.findByName(name);
        }

        @Operation(summary = "Get similar advertisements", description = "Returns a list of similar advertisements based on category, price, and ID.")
        @GetMapping("/similars")
        public List<AdvertisementResponse> getSimilars(@RequestParam("cat") UUID catId,
                        @RequestParam("price") double price,
                        @RequestParam("id") UUID id) {
                return service.findSimilars(catId, price, id);
        }

        @Operation(summary = "Get advertisements by category", description = "Returns a list of advertisements for the specified category.")
        @GetMapping("/cat/{id}")
        public List<AdvertisementResponse> getByCat(@PathVariable("id") UUID id) {
                return service.findByCategoryIdOrChildCategoryIds(id);
        }

        @Operation(summary = "Get advertisement by ID", description = "Returns the advertisement with the specified ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Advertisement found"),
                        @ApiResponse(responseCode = "404", description = "Advertisement not found")
        })
        @GetMapping("/add/{id}")
        public ResponseEntity<?> getAddById(@PathVariable("id") UUID id) {
                return ResponseEntity.ok(service.findById(id));
        }

        @GetMapping("/byCategory/{id}")
        public ResponseEntity<?> getByCategory(@PathVariable("id") UUID id) {
                return ResponseEntity.ok(service.findByCategoryIdOrChildCategoryIds(id));
        }
}
