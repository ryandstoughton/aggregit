package com.stoughton.ryan.aggregit.github;

import com.stoughton.ryan.aggregit.git.GitContributionDay;
import com.stoughton.ryan.aggregit.github.GithubContributions.ContributionsData.User.ContributionsCollection.ContributionCalendar.Week;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GithubService {

  private GithubClient githubClient;

  public GithubService(GithubClient githubClient) {
    this.githubClient = githubClient;
  }

  public Mono<List<GitContributionDay>> userContributions(String username) {
    return githubClient.userContributions(username).flatMap(githubContributions -> {
      AtomicInteger dayOfYear = new AtomicInteger(0);

      List<Week> contributionWeeks = githubContributions
          .getData()
          .getUser()
          .getContributionsCollection()
          .getContributionCalendar()
          .getWeeks();

      return Mono.just(contributionWeeks.stream()
          .flatMap(week -> week.getContributionDays()
              .stream()
              .map(day -> GitContributionDay.builder()
                  .count(day.getContributionCount())
                  .dayOfYear(dayOfYear.getAndIncrement())
                  .build()))
          .collect(Collectors.toList()));
    });
  }
}
