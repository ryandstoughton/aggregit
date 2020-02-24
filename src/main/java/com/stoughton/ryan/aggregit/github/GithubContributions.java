package com.stoughton.ryan.aggregit.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubContributions {

  private ContributionsData data;
  private List<ContributionErrors> errors;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ContributionsData {

    private User user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {

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

  @Data
  @Builder
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ContributionErrors {
    // Ignore actual errors for now - assume user is not found if errors are present
  }
}
