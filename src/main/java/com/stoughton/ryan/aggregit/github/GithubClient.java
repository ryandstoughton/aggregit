package com.stoughton.ryan.aggregit.github;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GithubClient {

  private WebClient webClient;

  public GithubClient(@Qualifier(value = "githubWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<Boolean> userExists(String username) {
    return webClient.post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(buildUserExistsQuery(username)))
        .retrieve()
        .bodyToMono(GithubUser.class)
        .map(githubUser -> githubUser.getData().getUser() != null);
  }

  public Mono<GithubContributions> userContributions(String username) {
    return webClient.post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(buildUserContributionsQuery(username)))
        .retrieve()
        .bodyToMono(GithubContributions.class);
  }

  private String buildUserExistsQuery(String username) {
    return "{\"query\": \"query { user(login: \\\"" + username + "\\\") { login } }\"}";
  }

  private String buildUserContributionsQuery(String username) {
    return "{\"query\": \"query { user(login: \\\"" + username
        + "\\\") { login contributionsCollection { contributionCalendar { weeks { contributionDays { contributionCount weekday } } } } } }\"}";
  }
}
