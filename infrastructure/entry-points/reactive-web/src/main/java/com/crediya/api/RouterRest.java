package com.crediya.api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    public static final Logger LOGGER = LoggerFactory.getLogger(RouterRest.class);


    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        LOGGER.debug("Entering to RouterFunction - Handler: {}", handler);
        return route(POST("/api/v1/solicitudes"), handler::listenPOSTRegisterLoanRequest)
                .andRoute(GET("/api/v1/solicitudes"), handler::listenGETLoanRequests);
    }
}
