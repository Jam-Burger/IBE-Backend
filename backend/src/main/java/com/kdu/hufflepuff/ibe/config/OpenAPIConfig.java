package com.kdu.hufflepuff.ibe.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Hufflepuff IBE API")
                .description("API documentation for the Hufflepuff IBE application.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Hufflepuff Team")
                )
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local Server"),
                new Server().url("https://ala2vbnbel.execute-api.ap-south-1.amazonaws.com/dev").description("Development Server"),
                new Server().url("https://rjkq6f2393.execute-api.ap-south-1.amazonaws.com/qa").description("Production Server")
            ));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/**")
            .build();
    }
}
