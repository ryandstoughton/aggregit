package com.stoughton.ryan.aggregit.gitlab;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

/*
 * Heavily references spring-framework's WebClientIntegrationTests.java for test configuration
 */
class GitlabClientIntegrationTest {

  private ObjectMapper mapper;

  private MockWebServer server;

  private WebClient webClient;

  private GitlabClient subject;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
  }

  @AfterEach
  void tearDown() throws IOException {
    this.server.shutdown();
  }

  @Test
  void userContributions_deserializesResponse()
      throws JsonProcessingException, InterruptedException, ParseException {
    startServer();
    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
    List<GitlabContribution> expectedBody = Lists.newArrayList(
        GitlabContribution.builder()
            .createdAt(dateTimeFormat.parse("2019-11-23T03:15:17.999Z"))
            .build()
    );
    String expectedBodyJson = mapper.writeValueAsString(expectedBody);

    prepareResponse(response -> response
        .setHeader("content-type", "application/json")
        .setBody(expectedBodyJson));

    StepVerifier.create(subject.userContributions("someuser"))
        .consumeNextWith(response -> assertThat(response).isEqualTo(expectedBody))
        .expectComplete()
        .verify();

    // Set timeout avoids unsatisfied test condition from hanging indefinitely
    RecordedRequest recordedRequest = this.server.takeRequest(3, TimeUnit.SECONDS);
    assertThat(recordedRequest.getRequestUrl().pathSegments())
        .isEqualTo(Lists.newArrayList("users", "someuser", "events"));
  }

  private void startServer() {
    this.server = new MockWebServer();
    this.webClient = WebClient.builder()
        .baseUrl(this.server.url("/").toString())
        .build();
    this.subject = new GitlabClient(webClient);
  }

  private void prepareResponse(Consumer<MockResponse> consumer) {
    MockResponse response = new MockResponse();
    consumer.accept(response);
    this.server.enqueue(response);
  }
}
