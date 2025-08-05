package com.hostel;

import com.hostel.service.DataInitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HostelManagementSystemApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(HostelManagementSystemApplication.class);

    private final DataInitializationService initializationService;

    public HostelManagementSystemApplication(DataInitializationService initializationService) {
        this.initializationService = initializationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(HostelManagementSystemApplication.class, args);
        logger.info("🚀 Hostel Management System API (MySQL) is running!");
        logger.info("📝 API Documentation: http://localhost:8080/swagger-ui.html");
    }

    @Override
    public void run(String... args) {
        initializationService.initializeData();
    }
}