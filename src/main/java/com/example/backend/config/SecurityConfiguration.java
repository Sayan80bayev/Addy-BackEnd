package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.formLogin(form -> form
                                .loginPage("http://localhost:3000/login")
                                .permitAll())
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/index").authenticated()
                                                .requestMatchers("api/v1/auth/**").permitAll()
                                                .anyRequest().permitAll())
                                .sessionManagement(
                                                (sessionManagement) -> sessionManagement
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}