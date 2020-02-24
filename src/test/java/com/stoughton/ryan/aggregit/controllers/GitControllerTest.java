package com.stoughton.ryan.aggregit.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stoughton.ryan.aggregit.controllers.GitController.UnsupportedPlatformException;
import com.stoughton.ryan.aggregit.github.GithubContributions;
import com.stoughton.ryan.aggregit.github.GithubContributions.ContributionsData;
import com.stoughton.ryan.aggregit.github.GithubContributions.ContributionsData.User;
import com.stoughton.ryan.aggregit.github.GithubService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GitControllerTest {

  @Mock
  private GithubService githubService;

  private GitController subject;

  @BeforeEach
  void setUp() {
    subject = new GitController(githubService);
  }

  @Test
  void userContributions_targetsGithub_callsGithubService() {
    String platform = "github";
    String username = "someuser";
    GithubContributions expectedGithubContributions = GithubContributions.builder()
        .data(ContributionsData.builder()
            .user(User.builder()
                .login(username)
                .build())
            .build())
        .build();
    when(githubService.userContributions(username))
        .thenReturn(Mono.just(expectedGithubContributions));

    StepVerifier.create(subject.userContributions(platform, username))
        .as("Call to subject that should produce some expected GithubContributions")
        .assertNext(githubContributions ->
            Assertions.assertEquals(githubContributions, expectedGithubContributions))
        .expectComplete()
        .verify();

    verify(githubService).userContributions(username);
  }

  @Test
  void userContributions_targetsUnsupportedPlatform_throwsException() {
    String platform = "badplatform";
    String username = "someuser";

    StepVerifier.create(subject.userContributions(platform, username))
        .as("Call to subject with an invalid platform value")
        .expectError(UnsupportedPlatformException.class)
        .verify();
  }
}
