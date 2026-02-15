package com.metamoteur.config;

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
 * Configuration Swagger/OpenAPI pour documentation API
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MetaMoteur API")
                        .version("2.0.0")
                        .description("""
                                API du méta-moteur de recherche orienté communauté.
                                
                                Fonctionnalités principales:
                                - Recherche multi-sources avec permutation intelligente
                                - Communication agent-à-agent
                                - Analytics et statistiques
                                - Tracking des clics
                                
                                Sécurité:
                                - Rate limiting: 20 requêtes/minute
                                - Validation stricte des entrées
                                - Protection CSRF
                                - Headers de sécurité
                                """)
                        .contact(new Contact()
                                .name("MetaMoteur Team")
                                .email("contact@metamoteur.com")
                                .url("https://github.com/Prjprj/metamoteur")
                        )
                        .license(new License()
                                .name("GPL-2.0")
                                .url("https://www.gnu.org/licenses/old-licenses/gpl-2.0.html")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local development"),
                        new Server()
                                .url("https://api.metamoteur.com")
                                .description("Production")
                ));
    }
}