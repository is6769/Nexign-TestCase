package org.example.roamingaggregatorservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Конфигурация для настройки валидации параметров методов.
 * <p>
 * Этот класс обеспечивает активацию валидации параметров методов,
 * аннотированных валидационными аннотациями, такими как 
 * {@code @Pattern}, {@code @Min}, {@code @Max} и т.д.
 * </p>
 * 
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
@Configuration
public class ValidationConfig {
    
    /**
     * Создает и настраивает post-процессор для валидации методов.
     * <p>
     * Данный бин позволяет Spring автоматически валидировать параметры методов,
     * отмеченные соответствующими аннотациями в контроллерах и сервисах.
     * </p>
     *
     * @return Настроенный post-процессор для валидации методов
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
