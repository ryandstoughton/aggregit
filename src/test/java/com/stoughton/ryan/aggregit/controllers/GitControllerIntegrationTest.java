package com.stoughton.ryan.aggregit.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stoughton.ryan.aggregit.git.GitContributionDay;
import com.stoughton.ryan.aggregit.github.GithubService;
import com.stoughton.ryan.aggregit.gitlab.GitlabService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(GitController.class)
class GitControllerIntegrationTest {

  WebTestClient webTestClient;

  @MockBean
  GithubService githubService;

  @MockBean
  GitlabService gitlabService;

  @BeforeEach()
  void setUp() {
    webTestClient = WebTestClient.bindToController(new GitController(githubService, gitlabService)).build();
  }

  @Test
  void userContributions_targetsGithub() {
    when(githubService.userExists(anyString())).thenReturn(Mono.just(true));
    when(githubService.userContributions(anyString()))
        .thenReturn(Mono.just(Lists.newArrayList(
            GitContributionDay.builder().build()
        )));

    webTestClient.get().uri("/git/contributions?username=someuser&platform=github")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk();

    verify(githubService).userExists("someuser");
    verify(githubService).userContributions("someuser");
  }

  @Test
  void userContributions_targetsGitlab() {
    when(gitlabService.userExists(anyString())).thenReturn(Mono.just(true));
    when(gitlabService.userContributions(anyString()))
        .thenReturn(Mono.just(Lists.newArrayList(
            GitContributionDay.builder().build()
        )));

    webTestClient.get().uri("/git/contributions?username=someuser&platform=gitlab")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk();

    verify(gitlabService).userExists("someuser");
    verify(gitlabService).userContributions("someuser");
  }

  @Test
  void userContributions_targetsUnsupportedPlatform() {
    webTestClient.get().uri("/git/contributions?username=someuser&platform=badplatform")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);

    verify(githubService, times(0)).userContributions("someuser");
  }
}
