package com.stoughton.ryan.aggregit.controllers;

import static org.mockito.Mockito.verify;

import com.stoughton.ryan.aggregit.github.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(GitController.class)
class GitControllerIntegrationTest {

  WebTestClient webTestClient;

  @MockBean
  GithubService githubService;

  @BeforeEach()
  void setUp() {
    webTestClient = WebTestClient.bindToController(new GitController(githubService)).build();
  }

  @Test
  void userContributions_targetsGithub() {
    webTestClient.get().uri("/git/contributions?username=someuser&platform=github")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk();

    verify(githubService).userContributions("someuser");
  }
}
