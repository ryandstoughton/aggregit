package com.stoughton.ryan.aggregit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Data
@Primary // AggregitProperties is getting defined twice somehow, fix this later
@Configuration
@ConfigurationProperties(prefix = "aggregit", ignoreUnknownFields = false)
public class AggregitProperties {

  @Data
  public static class Github {
    private String baseUrl;
    private String token;
  }

  private Github github;

}
