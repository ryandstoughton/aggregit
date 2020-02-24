package com.stoughton.ryan.aggregit.github;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GithubService {

  private GithubClient githubClient;

  public GithubService(GithubClient githubClient) {
    this.githubClient = githubClient;
  }

  public Mono<GithubContributions> userContributions(String username) {
    return githubClient.userContributions(username);
  }
}
