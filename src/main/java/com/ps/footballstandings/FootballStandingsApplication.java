package com.ps.footballstandings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableCaching
@EnableFeignClients(basePackages = "com.ps.footballstandings.client")
public class FootballStandingsApplication {

  public static void main(String[] args) {
    SpringApplication.run(FootballStandingsApplication.class, args);
  }
}
