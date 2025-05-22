package com.yl.wirebarley.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(OpenAPI.class)
public class CommonSwaggerConfig {

    @Bean
    public OpenAPI wirebarleyOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Wirebarley Banking API")
                .description("Wirebarley 은행 시스템 REST API 문서")
                .version("v1.0.0");
    }
}
