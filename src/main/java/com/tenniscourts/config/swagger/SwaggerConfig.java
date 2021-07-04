package com.tenniscourts.config.swagger;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket productApi() {

        return new Docket(SWAGGER_2)
            .select().apis(basePackage("com.tenniscourts"))
            .paths(PathSelectors.any())
            .build();
    }
}
