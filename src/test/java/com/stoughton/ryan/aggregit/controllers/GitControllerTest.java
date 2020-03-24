package com.stoughton.ryan.aggregit.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stoughton.ryan.aggregit.controllers.GitController.UnsupportedPlatformException;
import com.stoughton.ryan.aggregit.git.GitContributionDay;
import com.stoughton.ryan.aggregit.git.NoSuchUserException;
import com.stoughton.ryan.aggregit.github.GithubService;
import com.stoughton.ryan.aggregit.gitlab.GitlabService;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class GitControllerTest {

  @Mock
  private GithubService githubService;

  @Mock
  private GitlabService gitlabService;

  private GitController subject;

  @BeforeEach
  void setUp() {
    subject = new GitController(githubService, gitlabService);
  }

  @Test
  void userContributions_targetsGithub_callsGithubService() {
    String platform = "github";
    String username = "someuser";
    List<GitContributionDay> expectedGitContributions = Lists.newArrayList(
        GitContributionDay.builder().count(1).dayOfYear(0).build(),
        GitContributionDay.builder().count(3).dayOfYear(1).build()
    );
    when(githubService.userContributions(username))
        .thenReturn(Mono.just(expectedGitContributions));

    StepVerifier.create(subject.userContributions(platform, username))
        .assertNext(contributions ->
            Assertions.assertEquals(contributions, expectedGitContributions))
        .expectComplete()
        .verify();

    verify(githubService).userContributions(username);
  }

  @Test
  void userContributions_targetsGitlab_userExists_getContributions() {
    String platform = "gitlab";
    String username = "someuser";
    List<GitContributionDay> expectedGitContributions = Lists.newArrayList(
        GitContributionDay.builder().count(1).dayOfYear(0).build(),
        GitContributionDay.builder().count(3).dayOfYear(1).build()
    );
    when(gitlabService.userExists(username))
        .thenReturn(Mono.just(true));
    when(gitlabService.userContributions(username))
        .thenReturn(Mono.just(expectedGitContributions));

    StepVerifier.create(subject.userContributions(platform, username))
        .assertNext(contributions ->
            Assertions.assertEquals(contributions, expectedGitContributions))
        .expectComplete()
        .verify();

    verify(gitlabService).userExists(username);
    verify(gitlabService).userContributions(username);
  }

  @Test
  void userContributions_targetsGitlab_userDoesNotExist_throwsError() {
    String platform = "gitlab";
    String username = "someuser";
    List<GitContributionDay> expectedGitContributions = Lists.newArrayList(
        GitContributionDay.builder().count(1).dayOfYear(0).build(),
        GitContributionDay.builder().count(3).dayOfYear(1).build()
    );
    when(gitlabService.userExists(username))
        .thenReturn(Mono.just(false));

    StepVerifier.create(subject.userContributions(platform, username))
        .expectError(NoSuchUserException.class)
        .verify();

    verify(gitlabService).userExists(username);
    verifyNoMoreInteractions(gitlabService);
  }

  @Test
  void userContributions_targetsUnsupportedPlatform_throwsException() {
    String platform = "badplatform";
    String username = "someuser";

    StepVerifier.create(subject.userContributions(platform, username))
        .as("Call to subject with an invalid platform value")
        .expectError(UnsupportedPlatformException.class)
        .verify();
  }
}
