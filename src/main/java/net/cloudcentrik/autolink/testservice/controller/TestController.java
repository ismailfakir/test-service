package net.cloudcentrik.autolink.testservice.controller;

import lombok.extern.log4j.Log4j2;
import net.cloudcentrik.autolink.testservice.batch.DataBufferToInputStream;
import net.cloudcentrik.autolink.testservice.model.Greeting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Clock;
import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
@EnableAutoConfiguration
public class TestController {

    @Value("${server.port}")
    private Integer port;

    @Value("${spring.application.name}")
    private String name;

    @GetMapping("/test-api-call")
    public Mono<ServerResponse> getAllViews(){

        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            try {
                DataBufferToInputStream.testApiCall();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return ServerResponse.ok()
                .bodyValue(createGreeting());
    }

    Greeting createGreeting() {
        var now = Clock.systemDefaultZone().instant();
        return new Greeting("status: started API call on http://"+name+":"+port+"/ at:"+now);
    }
}
