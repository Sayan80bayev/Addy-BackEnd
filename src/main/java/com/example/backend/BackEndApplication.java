package com.example.backend;

import com.example.backend.config.ApplicationConfig;
import com.example.backend.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackEndApplication {
	public static void main(String[] args) {
		EnvConfig.loadEnv();
		SpringApplication.run(BackEndApplication.class, args);
	}

}
