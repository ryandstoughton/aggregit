package com.stoughton.ryan.aggregit.domain;


import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionErrors;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.User;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.User.ContributionsCollection;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.User.ContributionsCollection.ContributionCalendar;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.User.ContributionsCollection.ContributionCalendar.Week;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.User.ContributionsCollection.ContributionCalendar.Week.ContributionDay;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.synchronoss.cloud.nio.multipart.util.IOUtils;


public class GithubContributionsTest {

  private ObjectMapper mapper = new ObjectMapper();

  private String githubContributionsJson;
  private String githubContributionsWithErrorsJson;

  @BeforeEach
  void setUp() throws IOException {
    githubContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("githubContributions.json").getInputStream(),
        "UTF-8");
    githubContributionsWithErrorsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("githubContributionsWithErrors.json").getInputStream(),
        "UTF-8");
  }

  @Test
  void success_serializesGithubContributionsFromJson() throws JsonProcessingException {
    Week week1 = Week.builder()
        .contributionDays(Lists.newArrayList(
            ContributionDay.builder()
                .contributionCount(1)
                .weekday(0)
                .build(),
            ContributionDay.builder()
                .contributionCount(0)
                .weekday(1)
                .build()
        )).build();
    Week week2 = Week.builder()
        .contributionDays(Lists.newArrayList(
            ContributionDay.builder()
                .contributionCount(2)
                .weekday(0)
                .build(),
            ContributionDay.builder()
                .contributionCount(1)
                .weekday(1)
                .build()
        )).build();
    GithubContributions expected = GithubContributions.builder()
        .data(
            ContributionsData.builder()
                .user(
                    User.builder()
                        .login("ryan")
                        .contributionsCollection(
                            ContributionsCollection.builder()
                                .contributionCalendar(
                                    ContributionCalendar.builder()
                                        .weeks(Lists.newArrayList(week1, week2))
                                        .build())
                                .build())
                        .build())
                .build())
        .build();
    GithubContributions actual = mapper
        .readValue(githubContributionsJson, GithubContributions.class);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void error_serializesGithubContributionsFromJsonContainingErrorsList()
      throws JsonProcessingException {
    GithubContributions expected = GithubContributions.builder()
        .data(ContributionsData.builder()
            .user(null)
            .build())
        .errors(Lists.newArrayList(ContributionErrors.builder().build()))
        .build();
    GithubContributions actual = mapper
        .readValue(githubContributionsWithErrorsJson, GithubContributions.class);

    assertThat(actual).isEqualTo(expected);
  }
}
