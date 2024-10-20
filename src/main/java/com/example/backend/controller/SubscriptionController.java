package com.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.SubscribeRequest;
import com.example.backend.model.User;
import com.example.backend.service.SubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/subs")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @Operation(summary = "Subscribe a user to an advertisement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> subscribe(@RequestBody SubscribeRequest s) {
        service.subscribeUser(s);
        return ResponseEntity.ok().body("SUCCESS");
    }

    @Operation(summary = "Unsubscribe a user from an advertisement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unsubscription successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping
    public ResponseEntity<?> unSubscribe(@RequestBody SubscribeRequest s) {
        service.deleteSubs(s);
        return ResponseEntity.ok().body("SUCCESS");
    }

    @Operation(summary = "Get subscriptions of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getSubscribtion() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(service.getSubcribtionsByUserID(currentUser.getId()));
    }
}
