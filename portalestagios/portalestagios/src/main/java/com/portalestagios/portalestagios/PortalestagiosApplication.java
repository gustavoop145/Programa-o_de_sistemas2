package com.portalestagios.portalestagios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.portalestagios")
@EnableJpaRepositories(basePackages = "com.portalestagios")
@EntityScan(basePackages = "com.portalestagios")
public class PortalestagiosApplication {
    public static void main(String[] args) {
        SpringApplication.run(PortalestagiosApplication.class, args);
    }
}
