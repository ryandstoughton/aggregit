package com.stoughton.ryan.aggregit.github;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoughton.ryan.aggregit.domain.GithubContributions;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.User;
import java.io.IOException;
import java.util.function.Consumer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/*
 * Heavily references spring-framework's WebClientIntegrationTests.java for test configuration
 */
class GithubClientIntegrationTest {

  private ObjectMapper mapper;

  private MockWebServer server;

  private WebClient webClient;

  private GithubClient subject;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
  }

  @AfterEach
  void tearDown() throws IOException {
    this.server.shutdown();
  }

  @Test
  void getUserContributions() throws IOException {
    startServer();

    GithubContributions expectedBody = GithubContributions.builder()
        .data(ContributionsData.builder()
            .user(User.builder()
                .login("someuser")
                .build())
            .build())
        .build();
    String expectedBodyJson = mapper.writeValueAsString(expectedBody);

    prepareResponse(response -> response
        .setHeader("content-type", "application/json")
        .setBody(expectedBodyJson));

    StepVerifier.create(subject.getUserContributions("someuser"))
        .consumeNextWith(response -> assertThat(response).isEqualTo(expectedBody))
        .expectComplete()
        .verify();
  }

  private void startServer() {
    this.server = new MockWebServer();
    this.webClient = WebClient.builder()
        .baseUrl(this.server.url("/").toString())
        .build();
    this.subject = new GithubClient(webClient);
  }

  private void prepareResponse(Consumer<MockResponse> consumer) {
    MockResponse response = new MockResponse();
    consumer.accept(response);
    this.server.enqueue(response);
  }
}