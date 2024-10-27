package com.example.backend.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    public static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().load();

        String TIGRIS_ACCESS_KEY = dotenv.get("TIGRIS_ACCESS_KEY");
        String TIGRIS_BUCKET_NAME = dotenv.get("TIGRIS_BUCKET_NAME");
        String TIGRIS_SECRET_KEY = dotenv.get("TIGRIS_SECRET_KEY");
        String TIGRIS_URL = dotenv.get("TIGRIS_URL");

        System.setProperty("TIGRIS_ACCESS_KEY", TIGRIS_ACCESS_KEY);
        System.setProperty("TIGRIS_BUCKET_NAME", TIGRIS_BUCKET_NAME);
        System.setProperty("TIGRIS_SECRET_KEY", TIGRIS_SECRET_KEY);
        System.setProperty("TIGRIS_URL", TIGRIS_URL);
    }
}