package com.hostel;

import com.hostel.service.DataInitializationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class HostelManagementSystemApplication implements CommandLineRunner {

    private final DataInitializationService initializationService;

    public HostelManagementSystemApplication(DataInitializationService initializationService) {
        this.initializationService = initializationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HostelManagementSystemApplication.class, args);
        System.out.println("Hostel Management System API is running!");
        System.out.println("Default warden login: username: warden, password: warden123");
    }

    @Override
    public void run(String... args) {
        initializationService.initializeData();
    }
}