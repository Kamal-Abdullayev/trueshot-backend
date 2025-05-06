package com.trueshot.api_gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getRequest().getMethod();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getRawPath();

        log.info("➡️ Incoming Request: [{}] {}", method, path);

        return chain.filter(exchange)
                .doOnSuccess(done -> {
                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    log.info("⬅️ Response Status: {}", status != null ? status.value() : "UNKNOWN");
                })
                .doOnError(error -> {
                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    log.error("❌ Error during request [{} {}]: {}", method, path, error.getMessage());
                    log.error("Response status (if set): {}", status != null ? status.value() : "UNKNOWN");
                });
    }


    @Override
    public int getOrder() {
        return -1;
    }
}
