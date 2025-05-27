package tableOrder.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger springdoc-ui 구성 파일
 */
@SecurityScheme(
        name = "Access",                          // Swagger에서 보일 이름
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "access"                      // 실제 HTTP 헤더 이름
)
@Configuration
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .packagesToScan(
                        "tableOrder.analytics.controller",
                        "tableOrder.auth.controller",
                        "tableOrder.category.controller",
                        "tableOrder.menu.controller",
                        "tableOrder.orders.controller",
                        "tableOrder.ordersItem.controller",
                        "tableOrder.sales.controller",
                        "tableOrder.stores.controller",
                        "tableOrder.tables.controller",
                        "tableOrder.users.controller"
                )
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Gaeng's Order")
                .version("v0.0.1")
                .description("Gaeng's Order API 명세서입니다.");
        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}