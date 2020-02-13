package com.stoughton.ryan.aggregit.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubContributions {

  private ContributionsData data;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContributionsData {

    private Viewer viewer;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Viewer {

      private String login;
      private ContributionsCollection contributionsCollection;

      @Data
      @Builder
      @NoArgsConstructor
      @AllArgsConstructor
      public static class ContributionsCollection {

        private ContributionCalendar contributionCalendar;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ContributionCalendar {

          private List<Week> weeks;

          @Data
          @Builder
          @NoArgsConstructor
          @AllArgsConstructor
          public static class Week {

            private List<ContributionDay> contributionDays;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class ContributionDay {

              private Integer contributionCount;
              private Integer weekday;
            }
          }
        }
      }
    }
  }
}
