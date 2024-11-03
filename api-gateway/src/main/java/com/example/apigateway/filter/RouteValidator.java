package com.example.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/verify-email",
            "/api/auth/token",
            "/eureka/**",
            "/assets/**",
            "/actuator/prometheus",
            "/"
    );

    private final PathMatcher pathMatcher = new AntPathMatcher();

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()));
}
