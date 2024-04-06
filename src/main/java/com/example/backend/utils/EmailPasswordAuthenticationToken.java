package com.example.backend.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class EmailPasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private String email;

    public EmailPasswordAuthenticationToken(String email, String password) {
        super(email, password);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
