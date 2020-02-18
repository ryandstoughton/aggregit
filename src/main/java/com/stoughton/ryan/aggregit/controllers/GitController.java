package com.stoughton.ryan.aggregit.controllers;

import com.stoughton.ryan.aggregit.domain.GithubContributions;
import com.stoughton.ryan.aggregit.github.GithubService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(path = "/git")
public class GitController {

  private GithubService githubService;

  public GitController(GithubService githubService) {
    this.githubService = githubService;
  }

  @GetMapping(path = "/contributions", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<GithubContributions> userContributions(@RequestParam("username") String username) {
    return githubService.userContributions(username);
  }
}
