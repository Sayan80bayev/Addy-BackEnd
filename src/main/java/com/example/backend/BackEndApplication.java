package com.example.backend;

import com.example.backend.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

@Slf4j
@SpringBootApplication
public class BackEndApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackEndApplication.class, args);
		Jedis jedis = new Jedis("fly-addy-spring-redis.upstash.io", 6379);
		jedis.auth("f8684cea5aa244f2827d5619b4465c0a");
		log.info("__SUCCESS__");
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
	}
}
