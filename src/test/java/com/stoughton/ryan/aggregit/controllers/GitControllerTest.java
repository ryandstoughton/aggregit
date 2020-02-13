package com.stoughton.ryan.aggregit.controllers;

import com.stoughton.ryan.aggregit.github.GithubService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(GitController.class)
class GitControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  GithubService githubService;

  @Test
  void userContributions() {
    // FIXME: Write this test
  }
}
