package com.stoughton.ryan.aggregit.gitlab;

import com.google.common.collect.Lists;
import com.stoughton.ryan.aggregit.git.GitContributionDay;
import com.stoughton.ryan.aggregit.util.DateUtil;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GitlabService {
  private final int DAYS_IN_A_YEAR = 365;
  private final int GIT_CONTRIBUTION_DAYS_IN_A_YEAR = 366;

  private GitlabClient gitlabClient;
  private DateUtil dateUtil;

  public GitlabService(GitlabClient gitlabClient, DateUtil dateUtil) {
    this.gitlabClient = gitlabClient;
    this.dateUtil = dateUtil;
  }

  public Mono<Boolean> userExists(String username) {
    return gitlabClient.userExists(username);
  }

  public Mono<List<GitContributionDay>> userContributions(String username) {
    final LocalDate now = dateUtil.getCurrentDate();
    final List<Integer> contributionCount = Lists.newArrayList(
        Collections.nCopies(GIT_CONTRIBUTION_DAYS_IN_A_YEAR, 0));

    return gitlabClient.userContributions(username)
        .map(gitlabContributions -> {
          // Count all Gitlab Contributions within the last year
          gitlabContributions.forEach(gitlabContribution -> {
            final int contributionDay = getContributionDay(gitlabContribution, now);
            if (contributionDay >= 0) { // Ignore days a year or more ago
              contributionCount.set(contributionDay, contributionCount.get(contributionDay) + 1);
            }
          });

          // Convert contribution counts into GitContributionDays
          List<GitContributionDay> gitContributionDays = Lists.newArrayList();
          for (int i = 0; i < contributionCount.size(); i++) {
            gitContributionDays.add(GitContributionDay.builder()
                .dayOfYear(i)
                .count(contributionCount.get(i))
                .build());
          }

          return gitContributionDays;
        });
  }

  //                                  e.g. Mar 11th 2019       e.g. Mar 10th 2020
  // Indexes days based on past year. 0 = tomorrow 1 year ago, 365 -> today
  private int getContributionDay(final GitlabContribution gitlabContribution,
      final LocalDate time) {
    int daysBetween = (int) ChronoUnit.DAYS
        .between(getDateOfContribution(gitlabContribution), time);
    return DAYS_IN_A_YEAR - daysBetween;
  }

  private LocalDate getDateOfContribution(GitlabContribution gitlabContribution) {
    return LocalDate.parse(gitlabContribution.getCreatedAt());
  }
}
