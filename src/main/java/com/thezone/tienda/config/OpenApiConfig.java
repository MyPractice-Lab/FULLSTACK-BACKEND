package com.thezone.tienda.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI apiDeLaTienda() {
        SecurityScheme esquemaJwt = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Pega el token que devuelve POST /api/auth/login");

        Info info = new Info()
                .title("API de la tienda The Zone")
                .version("1.0")
                .description("Backend REST de la tienda de artículos gamer The Zone. "
                        + "Los endpoints de escritura y el panel de administración exigen un token JWT con rol administrador.");

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes(ESQUEMA, esquemaJwt))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
