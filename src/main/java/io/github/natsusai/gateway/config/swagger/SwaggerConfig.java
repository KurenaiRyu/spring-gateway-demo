package io.github.natsusai.gateway.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    /**
     * 网关应用名称
     */
    @Value("${spring.application.name}")
    private String appName;

    /**
     * swagger2默认的url后缀
     */
    public static final String SWAGGER2_URL = "/v2/api-docs";

    /**
     * 网关配置
     */
    private final GatewayProperties properties;

    public SwaggerConfig(GatewayProperties properties) {
        this.properties = properties;
    }

    @Primary
    @Bean
    @Lazy
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        List<SwaggerResource> resources = properties.getRoutes().stream()
            .map(route -> createResource(route.getId(), getRouteLocation(route)))
            .collect(Collectors.toList());
        //添加gateway
        resources.add(createResource(appName, SWAGGER2_URL));
        return () -> resources;
    }

    // You will certainly need to edit this
    private String getRouteLocation(RouteDefinition route) {
        return Optional.ofNullable(route.getPredicates().get(0).getArgs().values().toArray()[0])
            .map(String::valueOf)
            .map(s -> s.replace("/**", SWAGGER2_URL))
            .orElse(null);
    }

    private SwaggerResource createResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}