package org.example.roamingaggregatorservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

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
