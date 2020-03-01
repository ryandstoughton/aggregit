package com.stoughton.ryan.aggregit.github;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoughton.ryan.aggregit.git.GitContributionDay;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.synchronoss.cloud.nio.multipart.util.IOUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

  private ObjectMapper mapper = new ObjectMapper();
  private String gitContributionsJson;
  private String githubContributionsJson;

  @Mock
  private GithubClient githubClient;

  private GithubService subject;

  @BeforeEach
  void setUp() {
    subject = new GithubService(githubClient);
  }

  @Test
  void userContributions_callsGithubClient() throws IOException {
    String username = "someuser";

    gitContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("githubContributionsFullToGitContributions.json").getInputStream(),
        "UTF-8");

    List<GitContributionDay> expectedGitContributionDays = mapper
        .readValue(gitContributionsJson, new TypeReference<>() {
        });

    githubContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("githubContributionsFull.json").getInputStream(),
        "UTF-8");
    GithubContributions githubContributions = mapper
        .readValue(githubContributionsJson, GithubContributions.class);
    when(githubClient.userContributions(username))
        .thenReturn(Mono.just(githubContributions));

    StepVerifier.create(subject.userContributions(username))
        .as("Call to subject that should produce some expected GithubContributions")
        .assertNext(gitContributions ->
            Assertions.assertEquals(gitContributions, expectedGitContributionDays))
        .expectComplete()
        .verify();

    verify(githubClient).userContributions(username);
  }
}
