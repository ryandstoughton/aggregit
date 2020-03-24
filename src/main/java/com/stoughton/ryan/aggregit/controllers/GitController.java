package com.stoughton.ryan.aggregit.controllers;

import com.stoughton.ryan.aggregit.git.GitContributionDay;
import com.stoughton.ryan.aggregit.git.NoSuchUserException;
import com.stoughton.ryan.aggregit.github.GithubService;
import com.stoughton.ryan.aggregit.gitlab.GitlabService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(path = "/git")
public class GitController {

  private GithubService githubService;
  private GitlabService gitlabService;

  public GitController(GithubService githubService, GitlabService gitlabService) {
    this.githubService = githubService;
    this.gitlabService = gitlabService;
  }

  @GetMapping(path = "/contributions", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<List<GitContributionDay>> userContributions(
      @RequestParam("platform") String platform,
      @RequestParam("username") String username) {
    switch (platform) {
      case "github":
        return githubService.userExists(username)
            .flatMap(exists -> exists ?
                githubService.userContributions(username) : Mono.error(new NoSuchUserException()));
      case "gitlab":
        return gitlabService.userExists(username)
            .flatMap(exists -> exists ?
                gitlabService.userContributions(username) : Mono.error(new NoSuchUserException()));
      default:
        return Mono.error(new UnsupportedPlatformException());
    }
  }

  @ResponseStatus(
      value = HttpStatus.BAD_REQUEST,
      reason = "Unsupported platform")
  @ExceptionHandler(UnsupportedPlatformException.class)
  public void unsupportedPlatformExceptionHandler() {
  }

  @ResponseStatus(
      value = HttpStatus.NOT_FOUND,
      reason = "No user found")
  @ExceptionHandler(NoSuchUserException.class)
  public void noSuchUserExceptionHandler() {
  }

  protected static class UnsupportedPlatformException extends Exception {

  }
}

