package com.stoughton.ryan.aggregit.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(path = "/git")
public class GitController {

    @GetMapping(path = "/contributions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Integer> userContributions() {
        return Mono.just(3); // FIXME: Return the expected user info
    }
}
