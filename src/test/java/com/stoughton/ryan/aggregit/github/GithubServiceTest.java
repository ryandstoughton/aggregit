package com.stoughton.ryan.aggregit.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
  void userExists() {
    when(githubClient.userExists(anyString())).thenReturn(Mono.just(true));

    StepVerifier.create(subject.userExists("someuser"))
        .expectNext(true)
        .verifyComplete();
  }

  @Test
  void userContributions_callsGithubClient_mapsResultsToGeneric() throws IOException {
    String username = "someuser";

    gitContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("gitContributionsForGithubConversion.json").getInputStream(),
        "UTF-8");

    List<GitContributionDay> expectedGitContributionDays = Lists.newArrayList();
    for (int i = 0; i < 352; i++) {
      expectedGitContributionDays.add(GitContributionDay.builder()
          .dayOfYear(i)
          .count(0)
          .build());
    }
    expectedGitContributionDays.addAll(mapper
        .readValue(gitContributionsJson, new TypeReference<>() {
        }));

    githubContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("githubContributionsForGitConversion.json").getInputStream(),
        "UTF-8");
    GithubContributions githubContributions = mapper
        .readValue(githubContributionsJson, GithubContributions.class);
    when(githubClient.userContributions(username))
        .thenReturn(Mono.just(githubContributions));

    StepVerifier.create(subject.userContributions(username))
        .as("Call to subject that should produce some expected GitContributionsDays")
        .assertNext(gitContributions ->
            Assertions.assertEquals(gitContributions, expectedGitContributionDays))
        .expectComplete()
        .verify();

    verify(githubClient).userContributions(username);
  }

  @Test
  void userContributions_ignoresDaysOneYearOrMoreAgo() throws IOException {
    String username = "someuser";

    gitContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("gitContributionsForGithubConversion.json")
            .getInputStream(),
        "UTF-8");

    // Create expected list
    List<GitContributionDay> expectedGitContributionDays = Lists.newArrayList();
    for (int i = 0; i < 352; i++) {
      expectedGitContributionDays.add(GitContributionDay.builder()
          .dayOfYear(i)
          .count(0)
          .build());
    }
    expectedGitContributionDays.addAll(mapper
        .readValue(gitContributionsJson, new TypeReference<>() {
        }));
    assertThat(expectedGitContributionDays.size()).isEqualTo(366);


    githubContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("githubContributionsForGitConversionWithOldContributions.json")
            .getInputStream(),
        "UTF-8");
    GithubContributions githubContributions = mapper
        .readValue(githubContributionsJson, GithubContributions.class);
    when(githubClient.userContributions(username))
        .thenReturn(Mono.just(githubContributions));

    StepVerifier.create(subject.userContributions(username))
        .assertNext(gitContributions ->
            Assertions.assertEquals(expectedGitContributionDays, gitContributions))
        .expectComplete()
        .verify();

    verify(githubClient).userContributions(username);
  }
}
