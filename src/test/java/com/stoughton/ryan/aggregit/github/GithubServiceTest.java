package com.stoughton.ryan.aggregit.github;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stoughton.ryan.aggregit.domain.GithubContributions;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

  @Mock
  private GithubClient githubClient;

  private GithubService subject;

  @BeforeEach
  void setUp() {
    subject = new GithubService(githubClient);
  }

  @Test
  void userContributions_callsGithubClient() {
    String username = "someuser";
    GithubContributions expectedGithubContributions = GithubContributions.builder()
        .data(ContributionsData.builder()
            .user(User.builder()
                .login(username)
                .build())
            .build())
        .build();
    when(githubClient.userContributions(username))
        .thenReturn(Mono.just(expectedGithubContributions));

    StepVerifier.create(subject.userContributions(username))
        .as("Call to subject that should produce some expected GithubContributions")
        .assertNext(githubContributions ->
            Assertions.assertEquals(githubContributions, expectedGithubContributions))
        .expectComplete()
        .verify();

    verify(githubClient).userContributions(username);
  }
}
