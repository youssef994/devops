package com.example.apigateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RouteValidatorTest {

    private RouteValidator routeValidator;

    @BeforeEach
    public void setUp() {
        routeValidator = new RouteValidator();
    }

    @Test
    public void testOpenApiEndpoints() {
        // Testing an open endpoint
        ServerHttpRequest openRequest = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/auth/register")
                .build();

        assertFalse(routeValidator.isSecured.test(openRequest), "Expected /api/auth/register to be open");

        // Testing a wildcard open endpoint
        ServerHttpRequest eurekaRequest = MockServerHttpRequest
                .method(HttpMethod.GET, "/eureka/some-endpoint")
                .build();

        assertFalse(routeValidator.isSecured.test(eurekaRequest), "Expected /eureka/** to be open");
    }

    @Test
    public void testSecuredEndpoint() {
        // Testing a secured endpoint
        ServerHttpRequest securedRequest = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/secure/data")
                .build();

        assertTrue(routeValidator.isSecured.test(securedRequest), "Expected /api/secure/data to be secured");
    }

    @Test
    public void testNonMatchingEndpoint() {
        // Test an endpoint that does not match openApiEndpoints
        ServerHttpRequest nonMatchingRequest = MockServerHttpRequest
                .method(HttpMethod.GET, "/api/other-endpoint")
                .build();

        assertTrue(routeValidator.isSecured.test(nonMatchingRequest), "Expected /api/other-endpoint to be secured");
    }
}
