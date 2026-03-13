package com.haile.springrestfulapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIDocConfig {

    private SecurityScheme createBearerScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                   .scheme("bearer")
                                   .bearerFormat("JWT");
    }

    private Server createServer(String url, String description) {
        return new Server().url(url)
                           .description(description);
    }

    private Contact createContact() {
        return new Contact().name("Hải Lê")
                            .email("hai28022002@gmail.com")
                            .url("https://www.linkedin.com/in/haileinnotech/");
    }

    private License createLicense() {
        return new License().name("MIT License")
                            .url("https://choosealicense.com/licenses/mit/");
    }

    private Info createApiInfo() {
        return new Info().title("Spring Boot REST API")
                         .version("1.0")
                         .description("This API exposes all endpoints")
                         .contact(createContact())
                         .license(createLicense());
    }

    @Bean
    public OpenAPI myOpenAPI() {

        String securitySchemeName = "Bearer Authentication";

        return new OpenAPI().info(createApiInfo())
                            .servers(List.of(createServer("http://localhost:8080", "Development"),
                                             createServer("https://uat.example.com", "Testing"),
                                             createServer("https://production.example.com", "Production")))
                            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                            .components(new Components().addSecuritySchemes(securitySchemeName, createBearerScheme()));
    }
}