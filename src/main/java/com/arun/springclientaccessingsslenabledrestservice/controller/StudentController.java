package com.arun.springclientaccessingsslenabledrestservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * @author arun on 9/22/20
 */

@RestController
public class StudentController {

    private final WebClient studentSSLDisabledWebClient;
    private final WebClient studentWebClient;

    @Autowired
    public StudentController(WebClient studentSSLDisabledWebClient, WebClient studentWebClient) {
        this.studentSSLDisabledWebClient = studentSSLDisabledWebClient;
        this.studentWebClient = studentWebClient;
    }


    @GetMapping("/v2/student")
    public ResponseEntity<String> getStudent() {
        Flux<String> stringFlux = studentSSLDisabledWebClient.get().uri("https://localhost:8443/v1/student")
                .retrieve()
                .bodyToFlux(String.class);

        String studentDate = stringFlux.blockFirst();
        System.out.println(studentDate);
        return ResponseEntity.ok(studentDate);
    }

    @GetMapping("/v3/student")
    public ResponseEntity<String> getSSLEnabledStudent() {
        Flux<String> stringFlux = studentWebClient.get().uri("https://localhost:8443/v1/student")
                .retrieve()
                .bodyToFlux(String.class);

        String studentDate = stringFlux.blockFirst();
        System.out.println(studentDate);
        return ResponseEntity.ok(studentDate);
    }
}
