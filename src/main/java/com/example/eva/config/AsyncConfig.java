package com.example.eva.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de ejecución asíncrona opcional.
 * No cambia la funcionalidad existente: solo habilita métodos @Async que he añadido como auxiliares.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("correoExecutor")
    public Executor correoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("correo-");
        executor.initialize();
        return executor;
    }
}
