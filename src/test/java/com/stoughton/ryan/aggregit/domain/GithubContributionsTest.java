package com.stoughton.ryan.aggregit.domain;


import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.Viewer;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.Viewer.ContributionsCollection;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.Viewer.ContributionsCollection.ContributionCalendar;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.Viewer.ContributionsCollection.ContributionCalendar.Week;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.Viewer.ContributionsCollection.ContributionCalendar.Week.ContributionDay;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.synchronoss.cloud.nio.multipart.util.IOUtils;


public class GithubContributionsTest {

  private ObjectMapper mapper = new ObjectMapper();

  private String githubContributionsJson;

  @BeforeEach
  void setUp() throws IOException {
    githubContributionsJson = IOUtils.inputStreamAsString(
        new ClassPathResource("githubContributions.json").getInputStream(),
        "UTF-8");
  }

  @Test
  void serializesGithubContributionsFromJson() throws JsonProcessingException {
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
                .viewer(
                    Viewer.builder()
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
}
