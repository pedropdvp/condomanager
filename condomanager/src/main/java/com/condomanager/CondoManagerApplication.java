package com.condomanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada da aplicacao CondoManager SaaS.
 * @EnableScheduling ativa o motor de faturacao automatica de quotas (@Scheduled).
 */
@SpringBootApplication
@EnableScheduling
public class CondoManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CondoManagerApplication.class, args);
    }
}
