package com.stoughton.ryan.aggregit.gitlab;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class GitlabClient {

  private WebClient webClient;

  public GitlabClient(@Qualifier(value = "gitlabWebClient") WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<Boolean> userExists(String username) {
    return webClient.get()
        .uri("/users?username=" + username)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .flatMapMany(response -> response.bodyToFlux(GitlabUser.class))
        .collectList()
        .map(users -> users.size() == 1);
  }

  public Mono<List<GitlabContribution>> userContributions(String username) {
    Flux<GitlabContribution> gcFlux = webClient.get()
        .uri("/users/" + username + "/events")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .flatMapMany(response -> response.bodyToFlux(GitlabContribution.class));

    return gcFlux.collectList();
  }
}
