package com.ps.footballstandings.service;

import com.ps.footballstandings.client.FootballApiClient;
import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import com.ps.footballstandings.service.fallback.FallbackHelper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FootballServiceImpl implements FootballService {

  private final FootballApiClient apiClient;
  private final FallbackHelper fallbackHelper;

  @Value("${football.api.key}")
  private String apiKey;

  public FootballServiceImpl(FootballApiClient apiClient, FallbackHelper fallbackHelper) {
    this.apiClient = apiClient;
    this.fallbackHelper = fallbackHelper;
  }

  @Override
  @CircuitBreaker(name = "countries", fallbackMethod = "getCountriesFallback")
  @Retry(name = "countries")
  @Cacheable(value = "countries", key = "'all'")
  public List<Country> getCountries() {
    return apiClient.getCountries(apiKey);
  }

  public List<Country> getCountriesFallback(Throwable ex) {
    log.warn("Fallback: getCountries -> {}", ex.getMessage());
    return fallbackHelper.resolveFallback("countries", "all", Country.class);
  }

  @Override
  @CircuitBreaker(name = "leagues", fallbackMethod = "getLeaguesFallback")
  @Retry(name = "leagues")
  @Cacheable(value = "leagues", key = "#countryId")
  public List<League> getLeagues(String countryId) {
    return apiClient.getLeagues(countryId, apiKey);
  }

  public List<League> getLeaguesFallback(String countryId, Throwable ex) {
    log.warn("Fallback: getLeagues -> {}", ex.getMessage());
    return fallbackHelper.resolveFallback("leagues", countryId, League.class);
  }

  @Override
  @CircuitBreaker(name = "standings", fallbackMethod = "getStandingsFallback")
  @Retry(name = "standings")
  @Cacheable(value = "standings", key = "#leagueId")
  public List<Standing> getStandings(String leagueId) {
    return apiClient.getStandings(leagueId, apiKey);
  }

  public List<Standing> getStandingsFallback(String leagueId, Throwable ex) {
    log.warn("Fallback: getStandings -> {}", ex.getMessage());
    return fallbackHelper.resolveFallback("standings", leagueId, Standing.class);
  }
}
