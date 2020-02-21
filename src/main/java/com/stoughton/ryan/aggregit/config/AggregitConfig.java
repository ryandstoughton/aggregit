package com.stoughton.ryan.aggregit.config;

import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFlux
public class AggregitConfig {

  @Autowired
  private AggregitProperties properties;

  @Bean(name = "githubWebClient")
  public WebClient githubWebClient() {
    return WebClient.builder()
        .baseUrl(properties.getGithub().getBaseUrl())
        .defaultHeader(
            HttpHeaders.AUTHORIZATION,
            "Bearer " + properties.getGithub().getToken())
        .build();
  }

  @Bean(name = "gitlabWebClient")
  public WebClient gitlabWebClient() {
    return WebClient.builder()
        .baseUrl(properties.getGitlab().getBaseUrl())
        .defaultHeader(
            HttpHeaders.AUTHORIZATION,
            "Bearer " + properties.getGitlab().getToken())
        .build();
  }
}
