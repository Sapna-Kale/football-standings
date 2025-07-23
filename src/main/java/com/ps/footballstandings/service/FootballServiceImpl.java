package com.ps.footballstandings.service;

import com.ps.footballstandings.client.FootballApiClient;
import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FootballServiceImpl implements FootballService {

  private final FootballApiClient apiClient;
  private final FallbackService fallbackService;

  @Value("${apifootball.api.key}")
  private String apiKey;

  public FootballServiceImpl(FootballApiClient apiClient, FallbackService fallbackService) {
    this.apiClient = apiClient;
    this.fallbackService = fallbackService;
  }

  private List<Country> countryCache = new ArrayList<>();
  private final Map<String, List<League>> leagueCache = new HashMap<>();
  private final Map<String, List<Standing>> standingCache = new HashMap<>();

  public List<Country> getCountries() {
    try {
      List<Country> countries = apiClient.getCountries(apiKey);
      countryCache = countries;
      return countries;
    } catch (Exception ex) {
      log.error("API failed: getCountries. Returning cached or fallback.");
      return !countryCache.isEmpty() ? countryCache : fallbackService.getDefaultCountries();
    }
  }

  public List<League> getLeagues(String countryId) {
    try {
      List<League> leagues = apiClient.getLeagues(countryId, apiKey);
      leagueCache.put(countryId, leagues);
      return leagues;
    } catch (Exception ex) {
      log.error("API failed: getLeagues. Returning cached or fallback.");
      return leagueCache.getOrDefault(countryId, fallbackService.getDefaultLeagues());
    }
  }

  public List<Standing> getStandings(String leagueId) {
    try {
      List<Standing> standings = apiClient.getStandings(leagueId, apiKey);
      standingCache.put(leagueId, standings);
      return standings;
    } catch (Exception ex) {
      log.error("API failed: getStandings. Returning cached or fallback.");
      // Handle using global exception handler @RestControllerAdvice
      return standingCache.getOrDefault(leagueId, fallbackService.getDefaultStandings());
    }
  }
}
