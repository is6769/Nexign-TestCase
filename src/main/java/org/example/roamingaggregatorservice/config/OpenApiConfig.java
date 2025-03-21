package org.example.roamingaggregatorservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация OpenAPI для генерации документации API.
 * <p>
 * Этот класс настраивает компонент Swagger/OpenAPI, который
 * предоставляет автоматически генерируемую документацию API,
 * доступную через Swagger UI.
 * </p>
 * 
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает настроенный объект OpenAPI, который используется для генерации документации.
     * <p>
     * Метод задает основную информацию о API, включая название, версию,
     * описание, контактную информацию и определяет доступные серверы.
     * </p>
     *
     * @return Настроенный объект OpenAPI для документации
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Роуминг Агрегатор API")
                        .version("1.0.0")
                        .description("API для сервиса агрегации роуминговых данных. " +
                                "Предоставляет доступ к информации о записях данных пользователей (UDR) " +
                                "и позволяет генерировать записи данных вызовов (CDR).")
                        )
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Локальный сервер")
                ));
    }
}
