package com.airbng.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * 스웨거에서 jwt 토큰 설정가능하게 추가
     */

    @Bean
    public OpenAPI openAPI() {
        final String scheme = "Authorization";

        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes(
                                scheme,
                                new SecurityScheme()
                                        .name(scheme)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement().addList(scheme)
                );
    }
}
