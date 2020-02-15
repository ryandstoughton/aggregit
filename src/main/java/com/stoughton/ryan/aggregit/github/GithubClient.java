package com.stoughton.ryan.aggregit.github;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GithubClient {
  private WebClient webClient;

  public GithubClient(final WebClient webClient) {
    this.webClient = webClient;
  }

}
