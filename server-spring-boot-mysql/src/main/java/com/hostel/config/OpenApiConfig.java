package com.hostel.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hostelManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hostel Management System API (MySQL)")
                        .description("RESTful API for managing hostel operations including student registration, room allocation, food menu management, and room change requests. This version uses MySQL as the database with JPA/Hibernate.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Hostel Management Team")
                                .email("admin@hostel.com")
                                .url("https://hostel.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server (MySQL)"),
                        new Server()
                                .url("https://api-mysql.hostel.com")
                                .description("Production Server (MySQL)")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Enter JWT Bearer token in the format: Bearer {token}");
    }
} 