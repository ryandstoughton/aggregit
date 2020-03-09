package com.stoughton.ryan.aggregit.gitlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.stoughton.ryan.aggregit.git.GitContributionDay;
import com.stoughton.ryan.aggregit.util.DateUtil;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
class GitlabServiceTest {

  @Mock
  private GitlabClient gitlabClient;

  @Mock
  private DateUtil dateUtil;

  private ObjectMapper mapper = new ObjectMapper();
  private String gitContributionsJson;
  private String gitlabContributionsJson;

  private GitlabService subject;

  @BeforeEach
  void setUp() {
    subject = new GitlabService(gitlabClient, dateUtil);
  }

  @Test
  void userContributions_callsGitlabClient_mapsResultToGeneric() throws IOException {
    String username = "someuser";

    gitContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("gitContributionsForGitlabConversion.json").getInputStream(),
        "UTF-8");

    List<GitContributionDay> expectedGitContributionDays = Lists.newArrayList();
    for (int i = 0; i < 364; i++) {
      expectedGitContributionDays.add(GitContributionDay.builder()
          .count(0)
          .dayOfYear(i)
          .build());
    }
    expectedGitContributionDays.addAll(mapper
        .readValue(gitContributionsJson, new TypeReference<>() {
        }));

    gitlabContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("gitlabContributionsForGitConversion.json").getInputStream(),
        "UTF-8");

    List<GitlabContribution> expectedGitlabContributions = mapper
        .readValue(gitlabContributionsJson, new TypeReference<>() {
        });

    when(dateUtil.getCurrentDate())
        .thenReturn(LocalDate.parse("2020-11-21"));
    when(gitlabClient.userContributions(anyString()))
        .thenReturn(Mono.just(expectedGitlabContributions));

    StepVerifier.create(subject.userContributions(username))
        .assertNext(gitContributions -> assertThat(gitContributions)
            .isEqualTo(expectedGitContributionDays))
        .expectComplete()
        .verify();

    verify(gitlabClient).userContributions(username);
  }

  @Test
  void userContributions_ignoresDaysOneYearOrMoreAgo() throws IOException {
    String username = "someuser";

    gitContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("gitContributionsForGitlabConversion.json").getInputStream(),
        "UTF-8");

    List<GitContributionDay> expectedGitContributionDays = Lists.newArrayList();
    expectedGitContributionDays.add(GitContributionDay.builder()
        .count(1)
        .dayOfYear(0)
        .build());
    for (int i = 1; i < 364; i++) {
      expectedGitContributionDays.add(GitContributionDay.builder()
          .count(0)
          .dayOfYear(i)
          .build());
    }
    expectedGitContributionDays.addAll(mapper
        .readValue(gitContributionsJson, new TypeReference<>() {
        }));

    gitlabContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("gitlabContributionsForGitConversionWithOldContributions.json")
            .getInputStream(),
        "UTF-8");

    List<GitlabContribution> expectedGitlabContributions = mapper
        .readValue(gitlabContributionsJson, new TypeReference<>() {
        });

    when(dateUtil.getCurrentDate())
        .thenReturn(LocalDate.parse("2020-11-21"));
    when(gitlabClient.userContributions(anyString()))
        .thenReturn(Mono.just(expectedGitlabContributions));

    StepVerifier.create(subject.userContributions(username))
        .assertNext(gitContributions -> assertThat(gitContributions)
            .isEqualTo(expectedGitContributionDays))
        .expectComplete()
        .verify();

    verify(gitlabClient).userContributions(username);
  }
}
