package net.cloudcentrik.autolink.testservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController("/test")
@EnableAutoConfiguration
public class TestController {
    @GetMapping
    public String testService(HttpServletRequest request) {
        log.info("I am " + request.getRequestURL().toString());
        return request.getRequestURL().toString();
    }
}
