package com.ps.footballstandings.service;

import com.ps.footballstandings.client.FootballApiClient;
import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FootballServiceImpl implements FootballService {

  private final FootballApiClient apiClient;
  private final FallbackService fallbackService;
  private final CacheManager cacheManager;

  @Value("${football.api.key}")
  private String apiKey;

  public FootballServiceImpl(
      FootballApiClient apiClient, FallbackService fallbackService, CacheManager cacheManager) {
    this.apiClient = apiClient;
    this.fallbackService = fallbackService;
    this.cacheManager = cacheManager;
  }

  @Override
  @CircuitBreaker(name = "football-countries-api", fallbackMethod = "getCountriesFallback")
  @Retry(name = "football-countries-api")
  @Cacheable(value = "countries", key = "'all'")
  public List<Country> getCountries() {
    return apiClient.getCountries(apiKey);
  }

  public List<Country> getCountriesFallback(Throwable ex) {
    log.warn("Fallback triggered for getCountries: {}", ex.getMessage());
    var cache = cacheManager.getCache("countries");
    if (cache != null) {
      List<Country> cached = cache.get("all", List.class);
      if (cached != null && !cached.isEmpty()) {
        log.info("Serving from cache for countries.");
        return cached;
      }
    }
    log.warn("No cache found — serving fallback countries.");
    return fallbackService.getDefaultCountries();
  }

  @Override
  @CircuitBreaker(name = "football-leagues-api", fallbackMethod = "getLeaguesFallback")
  @Retry(name = "football-leagues-api")
  @Cacheable(value = "leagues", key = "#countryId")
  public List<League> getLeagues(String countryId) {
    return apiClient.getLeagues(countryId, apiKey);
  }

  public List<League> getLeaguesFallback(String countryId, Throwable ex) {
    log.warn("Fallback triggered for getLeagues: {}", ex.getMessage());
    var cache = cacheManager.getCache("leagues");
    if (cache != null) {
      List<League> cached = cache.get(countryId, List.class);
      if (cached != null && !cached.isEmpty()) {
        return cached;
      }
    }
    return fallbackService.getDefaultLeagues();
  }

  @Override
  @CircuitBreaker(name = "football-standings-api", fallbackMethod = "getStandingsFallback")
  @Retry(name = "football-standings-api")
  @Cacheable(value = "standings", key = "#leagueId")
  public List<Standing> getStandings(String leagueId) {
    return apiClient.getStandings(leagueId, apiKey);
  }

  public List<Standing> getStandingsFallback(String leagueId, Throwable ex) {
    log.warn("Fallback triggered for getStandings: {}", ex.getMessage());
    var cache = cacheManager.getCache("standings");
    if (cache != null) {
      List<Standing> cached = cache.get(leagueId, List.class);
      if (cached != null && !cached.isEmpty()) {
        return cached;
      }
    }
    return fallbackService.getDefaultStandings();
  }
}
