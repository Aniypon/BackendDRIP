package org.example.orderservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {
    
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50) // Автоматический выключатель срабатывает, если 50% вызовов неудачны
                        .waitDurationInOpenState(Duration.ofSeconds(10)) // Ждать 10 секунд перед повторной попыткой
                        .slidingWindowSize(10) // Учитывать последние 10 вызовов для расчета коэффициента отказов
                        .permittedNumberOfCallsInHalfOpenState(3) // Разрешить 3 вызова в полуоткрытом состоянии
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(5)) // Таймаут после 5 секунд
                        .build())
                .build());
    }
}
