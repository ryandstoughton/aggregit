package com.stoughton.ryan.aggregit.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoughton.ryan.aggregit.domain.GithubContributions;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData;
import com.stoughton.ryan.aggregit.domain.GithubContributions.ContributionsData.Viewer;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.reactive.function.client.WebClient;

class GithubClientTest {

  ObjectMapper mapper = new ObjectMapper();

  WebClient client;
  GithubClient subject;

  @Test
  void getUserContributions() throws IOException {
    MockWebServer githubServer = new MockWebServer();
    githubServer.enqueue(new MockResponse().setBody(
        mapper.writeValueAsString(
            GithubContributions.builder()
                .data(ContributionsData.builder()
                    .viewer(Viewer.builder()
                        .login("someuser")
                        .build())
                    .build()))));

    githubServer.start();
    HttpUrl url = githubServer.url("");

    client = WebClient.builder()
        .baseUrl(url.toString())
        .build();
    subject = new GithubClient(client);
  }
}