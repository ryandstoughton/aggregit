package com.stoughton.ryan.aggregit.github;

import com.stoughton.ryan.aggregit.domain.GithubContributions;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GithubClient {

  private WebClient webClient;

  public GithubClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<GithubContributions> getUserContributions(String username) {
    return webClient.post()
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(buildUserContributionsQuery(username)))
        .retrieve()
        .bodyToMono(GithubContributions.class);
  }

  private String buildUserContributionsQuery(String username) {
    return "\"query\": \"query {\n"
        + "  user(login: \"" + username + "\") {\n"
        + "    login\n"
        + "    contributionsCollection {\n"
        + "      contributionCalendar {\n"
        + "        weeks {\n"
        + "          contributionDays {\n"
        + "            contributionCount\n"
        + "            weekday\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "}\n\"";
  }
}
