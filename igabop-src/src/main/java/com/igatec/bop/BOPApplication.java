package com.igatec.bop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.igatec.bop", "com.igatec.utilsspring"})
public class BOPApplication {
    public static void main(String[] args) {
        SpringApplication.run(BOPApplication.class, args);
    }
}
