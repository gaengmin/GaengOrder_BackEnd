package tableOrder.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger springdoc-ui 구성 파일
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .packagesToScan(
                        "tableOrder.users.controller",
                        "tableOrder.stores.controller",
                        "tableOrder.menu.controller"
                )
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Gaeng's Order 다이어리")
                .version("v0.0.1")
                .description("Gaeng's Order API 명세서입니다.");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}