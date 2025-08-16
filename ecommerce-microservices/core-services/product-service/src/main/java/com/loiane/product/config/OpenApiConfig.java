package com.loiane.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Product Service API documentation.
 *
 * @author Loiane Groner
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Product Service}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("""
                            E-commerce Product Management Service

                            This service provides comprehensive product and category management capabilities
                            for an e-commerce platform. It includes advanced search features, category
                            hierarchy management, and full CRUD operations.

                            ## Key Features
                            - Advanced product search with multiple filters
                            - Hierarchical category management
                            - Product lifecycle management
                            - Comprehensive validation
                            - Pagination support for all list endpoints

                            ## Authentication
                            Currently, this service does not require authentication. Authentication will be
                            added in future versions.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Loiane Groner")
                                .email("loiane@example.com")
                                .url("https://github.com/loiane"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development server"),
                        new Server()
                                .url("https://api.ecommerce.example.com")
                                .description("Production server")
                ));
    }
}
