package com.loiane.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {
        var contact = new Contact()
                .name("Loiane Groner")
                .email("contact@loiane.com")
                .url("https://github.com/loiane");

        var license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        var info = new Info()
                .title("Product Service API")
                .version("1.0.0")
                .description("RESTful API for managing products and categories in the ecommerce platform")
                .contact(contact)
                .license(license);

        var devServer = new Server()
                .url("http://localhost:8080")
                .description("Development server");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}