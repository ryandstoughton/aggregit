package com.stoughton.ryan.aggregit.github;

import com.stoughton.ryan.aggregit.git.GitContributionDay;
import com.stoughton.ryan.aggregit.github.GithubContributions.ContributionsData.User.ContributionsCollection.ContributionCalendar.Week;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GithubService {
  private final int GIT_CONTRIBUTION_DAYS_IN_A_YEAR = 366;

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

      Mono<List<GitContributionDay>> days = Mono.just(contributionWeeks.stream()
          .flatMap(week -> {
            Stream<GitContributionDay> gitContributionDayStream = week.getContributionDays()
                .stream()
                .map(day -> GitContributionDay.builder()
                    .count(day.getContributionCount())
                    .dayOfYear(dayOfYear.getAndIncrement())
                    .build());
            log.info(">> days: {}", gitContributionDayStream);
            return gitContributionDayStream;
          })
          .collect(Collectors.toList()));

      if (dayOfYear.get() > GIT_CONTRIBUTION_DAYS_IN_A_YEAR) { // Too many days mapped
        days = days.flatMap(gitContributionDays -> {
          AtomicInteger numDays = new AtomicInteger(gitContributionDays.size());
          AtomicInteger newDayOfYear = new AtomicInteger(0);
          gitContributionDays.removeIf(ignored -> numDays.getAndDecrement() > GIT_CONTRIBUTION_DAYS_IN_A_YEAR);
          gitContributionDays.forEach(day -> day.setDayOfYear(newDayOfYear.getAndIncrement()));
          return Mono.just(gitContributionDays);
        });
      }
      return days;
    });
  }
}
