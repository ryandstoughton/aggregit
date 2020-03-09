package com.stoughton.ryan.aggregit.gitlab;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.synchronoss.cloud.nio.multipart.util.IOUtils;

class GitlabContributionTest {

  private ObjectMapper mapper;
  private String gitlabContributionJson;

  @BeforeEach
  void setUp() throws IOException {
    mapper = new ObjectMapper();
    gitlabContributionJson = IOUtils.inputStreamAsString(
        new ClassPathResource("gitlabContribution.json").getInputStream(),
        "UTF-8");
  }

  @Test
  void deserializesGitlabContributionJson_trimsDateStringTo10Characters()
      throws JsonProcessingException {
    GitlabContribution expected = GitlabContribution.builder()
        .createdAt("2019-11-20")
        .build();
    GitlabContribution actual = mapper
        .readValue(gitlabContributionJson, GitlabContribution.class);
    assertThat(actual).isEqualTo(expected);
  }
}
