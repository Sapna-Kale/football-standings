package com.ps.footballstandings.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ps.footballstandings.client.FootballApiClient;
import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

class FootballServiceImplTest {

  @Mock private FootballApiClient apiClient;

  @Mock private FallbackService fallbackService;

  @InjectMocks private FootballServiceImpl footballService;

  @Mock private CacheManager cacheManager;

  private final String apiKey = "9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    footballService = new FootballServiceImpl(apiClient, fallbackService, cacheManager);
    ReflectionTestUtils.setField(footballService, "apiKey", apiKey);
  }

  @Test
  @DisplayName("Test getCountry success api response")
  void testGetCountries_returnsCountriesFromApi() {
    List<Country> mockCountries =
        List.of(
            new Country("1", "Spain", "country_logo"), new Country("2", "Italy", "country_logo"));

    when(apiClient.getCountries(anyString())).thenReturn(mockCountries);

    List<Country> result = footballService.getCountries();

    assertEquals(2, result.size());
    verify(apiClient, times(1)).getCountries(anyString());
  }

  @Test()
  @DisplayName("Test getCountry fallback api response")
  void testGetCountriesFallback_returnsFallbackData() {
    List<Country> fallback = List.of(new Country("0", "Fallback", "country_logo"));
    when(fallbackService.getDefaultCountries()).thenReturn(fallback);

    List<Country> result = footballService.getCountriesFallback(new RuntimeException("API down"));

    assertEquals(1, result.size());
    assertEquals("Fallback", result.get(0).getCountry_name());
  }

  @Test
  @DisplayName("Test getLeague success api response")
  void testGetLeagues_shouldReturnLeaguesFromApi() {
    String countryId = "123";
    List<League> mockLeagues =
        List.of(
            new League(
                countryId, "England", "44", "Non League Premier", "2024/2025", "league_logo"));

    when(apiClient.getLeagues(countryId, apiKey)).thenReturn(mockLeagues);

    List<League> result = footballService.getLeagues(countryId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Non League Premier", result.get(0).getLeague_name());
    verify(apiClient, times(1)).getLeagues(eq(countryId), anyString());
  }

  @Test
  @DisplayName("Test getLeague fallback api response")
  void testGetLeaguesFallback_shouldReturnFromFallbackOrCache() {
    String countryId = "123";
    List<League> fallbackLeagues =
        List.of(
            new League(countryId, "44", "England", "Fallback League", "2024/2025", "league_logo"));
    when(fallbackService.getDefaultLeagues()).thenReturn(fallbackLeagues);

    List<League> result =
        footballService.getLeaguesFallback(countryId, new RuntimeException("Simulated"));

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals("Fallback League", result.get(0).getLeague_name());
  }

  @Test
  @DisplayName("Test getStanding success api response")
  void testGetStandings_shouldReturnStandingsFromApi() {
    String leagueId = "789";
    List<Standing> mockStandings =
        List.of(
            new Standing(
                "England",
                leagueId,
                "Non League Premier",
                "3035",
                "Team A",
                "Promotion",
                "team_badge"));

    when(apiClient.getStandings(leagueId, apiKey)).thenReturn(mockStandings);

    List<Standing> result = footballService.getStandings(leagueId);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Team A", result.get(0).getTeam_name());
    verify(apiClient).getStandings(eq(leagueId), anyString());
  }

  @Test
  @DisplayName("Test getStanding fallback api response")
  void testGetStandingsFallback_shouldReturnFromFallbackOrCache() {
    String leagueId = "789";
    List<Standing> fallback =
        List.of(
            new Standing(
                "England",
                leagueId,
                "Non League Premier",
                "3035",
                "Fallback Team",
                "Promotion",
                "team_badge"));
    when(fallbackService.getDefaultStandings()).thenReturn(fallback);

    List<Standing> result =
        footballService.getStandingsFallback(leagueId, new RuntimeException("Simulated"));

    assertEquals(1, result.size());
    assertEquals("Fallback Team", result.get(0).getTeam_name());
  }
}
