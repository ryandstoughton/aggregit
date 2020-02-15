package com.stoughton.ryan.aggregit;

import com.stoughton.ryan.aggregit.config.AggregitProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AggregitProperties.class)
public class AggregitApplication {

  public static void main(String[] args) {
    SpringApplication.run(AggregitApplication.class, args);
  }

}
