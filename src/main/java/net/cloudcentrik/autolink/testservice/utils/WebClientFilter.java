package net.cloudcentrik.autolink.testservice.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Log4j2
public class WebClientFilter {
    public static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            logMethodAndUrl(request);
            logHeaders(request);

            return Mono.just(request);
        });
    }


    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            logStatus(response);
            logHeaders(response);

            return Mono.just(response);
        });
    }


    public static boolean is5xxException(Throwable ex) {
        boolean eligible = false;

        if (ex instanceof ServiceException) {
            ServiceException se = (ServiceException)ex;
            eligible = (se.getStatusCode() > 499 && se.getStatusCode() < 600);
        }

        return eligible;
    };


    private static void logStatus(ClientResponse response) {
        HttpStatusCode status = response.statusCode();
        log.debug("Returned status code {} ({})", status.value(), status.toString());
    }


    public static ExchangeFilterFunction handleError() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (response.statusCode() != null && (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError())) {
                return response.bodyToMono(String.class)
                        .defaultIfEmpty(response.statusCode().toString())
                        .flatMap(body -> {
                            log.debug("Body is {}", body);
                            return Mono.error(new ServiceException(body, response.statusCode().value()));
                        });
            } else {
                return Mono.just(response);
            }
        });
    }


    private static void logHeaders(ClientResponse response) {
        response.headers().asHttpHeaders().forEach((name, values) -> {
            values.forEach(value -> {
                logNameAndValuePair(name, value);
            });
        });
    }


    private static void logHeaders(ClientRequest request) {
        request.headers().forEach((name, values) -> {
            values.forEach(value -> {
                logNameAndValuePair(name, value);
            });
        });
    }


    private static void logNameAndValuePair(String name, String value) {
        log.debug("{}={}", name, value);
    }


    private static void logMethodAndUrl(ClientRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.method().name());
        sb.append(" to ");
        sb.append(request.url());

        log.debug(sb.toString());
    }
}
