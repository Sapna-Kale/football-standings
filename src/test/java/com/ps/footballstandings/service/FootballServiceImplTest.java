package com.ps.footballstandings.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ps.footballstandings.client.FootballApiClient;
import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import com.ps.footballstandings.service.fallback.FallbackHelper;
import com.ps.footballstandings.service.fallback.FallbackService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FootballServiceImplTest {

  @Mock private FootballApiClient apiClient;

  @Mock private CacheManager cacheManager;

  @Mock private FallbackService fallbackService;

  @InjectMocks private FallbackHelper fallbackHelper;

  private FootballServiceImpl footballService;

  private final String apiKey = "9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978";

  @BeforeEach
  void setUp() {
    fallbackHelper = new FallbackHelper(cacheManager, fallbackService);
    footballService = new FootballServiceImpl(apiClient, fallbackHelper);
    ReflectionTestUtils.setField(footballService, "apiKey", apiKey);
  }

  @Test
  @DisplayName("Get countries - API success")
  void testGetCountries_returnsApiData() {
    List<Country> countries = List.of(new Country("1", "Brazil", "logo"));
    when(apiClient.getCountries(apiKey)).thenReturn(countries);

    List<Country> result = footballService.getCountries();

    assertEquals(1, result.size());
    verify(apiClient, times(1)).getCountries(apiKey);
  }

  @Test
  @DisplayName("Get leagues - API success")
  void testGetLeagues_returnsApiData() {
    List<League> leagues = List.of(new League("123", "44", "England", "League A", "2024", "logo"));
    when(apiClient.getLeagues("123", apiKey)).thenReturn(leagues);

    List<League> result = footballService.getLeagues("123");

    assertEquals(1, result.size());
    assertEquals("League A", result.get(0).getLeague_name());
  }

  @Test
  @DisplayName("Get standings - API success")
  void testGetStandings_returnsApiData() {
    List<Standing> standings =
        List.of(new Standing("England", "123", "League A", "1", "Team X", "Promoted", "logo"));
    when(apiClient.getStandings("123", apiKey)).thenReturn(standings);

    List<Standing> result = footballService.getStandings("123");

    assertEquals(1, result.size());
    assertEquals("Team X", result.get(0).getTeam_name());
  }

  @Test
  @DisplayName("Fallback: countries - no cache, fallback used")
  void testCountriesFallback_usesFallbackWhenNoCache() {
    when(cacheManager.getCache("countries")).thenReturn(null);
    when(fallbackService.getDefaultCountries())
        .thenReturn(List.of(new Country("0", "Fallback", "logo")));

    List<Country> result = footballService.getCountriesFallback(new RuntimeException("fail"));

    assertEquals(1, result.size());
    assertEquals("Fallback", result.get(0).getCountry_name());
  }

  @Test
  @DisplayName("Fallback: leagues - returns from fallback")
  void testLeaguesFallback_returnsFromFallback() {
    when(cacheManager.getCache("leagues")).thenReturn(null);
    when(fallbackService.getDefaultLeagues())
        .thenReturn(
            List.of(new League("123", "999", "Country", "Fallback League", "2024", "logo")));

    List<League> result = footballService.getLeaguesFallback("123", new RuntimeException("fail"));

    assertEquals(1, result.size());
    assertEquals("Fallback League", result.get(0).getLeague_name());
  }

  @Test
  @DisplayName("Fallback: standings - cache available")
  void testStandingsFallback_returnsFromCache() {
    String leagueId = "555";
    List<Standing> cachedData =
        List.of(new Standing("England", leagueId, "League A", "5", "Team Z", "Mid", "badge"));

    Cache cacheMock = mock(Cache.class);
    when(cacheManager.getCache("standings")).thenReturn(cacheMock);
    when(cacheMock.get(eq(leagueId), eq(List.class))).thenReturn(cachedData);

    List<Standing> result =
        footballService.getStandingsFallback(leagueId, new RuntimeException("fail"));

    assertEquals("Team Z", result.get(0).getTeam_name());
  }

  @Test
  @DisplayName("Fallback: standings - no cache, fallback used")
  void testStandingsFallback_usesFallback() {
    String leagueId = "555";

    when(cacheManager.getCache("standings")).thenReturn(null);
    when(fallbackService.getDefaultStandings())
        .thenReturn(
            List.of(
                new Standing(
                    "FallbackCountry",
                    leagueId,
                    "Fallback League",
                    "9",
                    "FallbackTeam",
                    "None",
                    "logo")));

    List<Standing> result =
        footballService.getStandingsFallback(leagueId, new RuntimeException("down"));

    assertEquals("FallbackTeam", result.get(0).getTeam_name());
  }

  @Test
  @DisplayName("Fallback: empty cache, fallback used")
  void testFallback_whenCacheEmpty_usesFallback() {
    String countryId = "1";

    Cache cache = mock(Cache.class);
    when(cacheManager.getCache("leagues")).thenReturn(cache);
    when(cache.get(countryId, List.class)).thenReturn(Collections.emptyList());

    when(fallbackService.getDefaultLeagues())
        .thenReturn(
            List.of(new League("1", "100", "Fallback", "EmptyFallbackLeague", "2024", "logo")));

    List<League> result =
        footballService.getLeaguesFallback(countryId, new RuntimeException("fail"));

    assertEquals("EmptyFallbackLeague", result.get(0).getLeague_name());
  }

  @Test
  @DisplayName("Fallback: unknown type returns empty list")
  void testFallback_unknownType_returnsEmptyList() {
    List result = fallbackHelper.resolveFallback("abc", "key", String.class);
    assertTrue(result.isEmpty());
  }
}
