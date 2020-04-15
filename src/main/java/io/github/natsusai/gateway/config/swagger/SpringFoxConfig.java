package io.github.natsusai.gateway.config.swagger;

import io.swagger.models.auth.In;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@Configuration
@EnableSwagger2WebFlux
public class SpringFoxConfig {

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
            .securitySchemes(Collections.singletonList(apiKey()));
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
            .title("BeeX Gateway API Doc")
            .description("This is a restful api document of Swagger.")
            .version("1.0")
            .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("Token", HttpHeaders.AUTHORIZATION, In.HEADER.name());
    }

}