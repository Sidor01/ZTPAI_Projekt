package org.example.skillwheel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI skillWheelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SkillWheel API")
                        .description("API dokumentacja dla aplikacji SkillWheel")
                        .version("1.0"));
    }
}