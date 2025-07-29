package com.ps.footballstandings.service.fallback;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

class FallbackHelperTest {

  @Mock private CacheManager cacheManager;
  @Mock private FallbackService fallbackService;
  @Mock private Cache mockCache;

  private FallbackHelper fallbackHelper;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    fallbackHelper = new FallbackHelper(cacheManager, fallbackService);
  }

  @Test
  void testResolveCountriesFromCache() {
    List<Country> cachedCountries = List.of(new Country("1", "Spain", "logo"));
    when(cacheManager.getCache("countries")).thenReturn(mockCache);
    when(mockCache.get("all", List.class)).thenReturn(cachedCountries);

    List<Country> result = fallbackHelper.resolveFallback("countries", "all", Country.class);
    assertEquals("Spain", result.get(0).getCountry_name());
  }

  @Test
  void testResolveCountriesFromFallback() {
    when(cacheManager.getCache("countries")).thenReturn(null);
    when(fallbackService.getDefaultCountries())
        .thenReturn(List.of(new Country("0", "Fallback", "logo")));

    List<Country> result = fallbackHelper.resolveFallback("countries", "all", Country.class);
    assertEquals("Fallback", result.get(0).getCountry_name());
  }

  @Test
  void testResolveUnknownTypeReturnsEmptyList() {
    List result = fallbackHelper.resolveFallback("dummy", "key", String.class);
    assertTrue(result.isEmpty());
  }

  @Test
  void testResolveLeaguesFromFallback_whenCacheEmpty() {
    when(cacheManager.getCache("leagues")).thenReturn(mockCache);
    when(mockCache.get("123", List.class)).thenReturn(null);
    when(fallbackService.getDefaultLeagues())
        .thenReturn(List.of(new League("123", "1", "Fallback", "League", "2024", "logo")));

    List<League> result = fallbackHelper.resolveFallback("leagues", "123", League.class);
    assertEquals("League", result.get(0).getLeague_name());
  }

  @Test
  void testResolveStandingsFromFallback_whenCacheEmpty() {
    when(cacheManager.getCache("standings")).thenReturn(mockCache);
    when(mockCache.get("456", List.class)).thenReturn(null);
    when(fallbackService.getDefaultStandings())
        .thenReturn(
            List.of(
                new Standing("England", "456", "League", "5", "Fallback Team", "None", "logo")));

    List<Standing> result = fallbackHelper.resolveFallback("standings", "456", Standing.class);
    assertEquals("Fallback Team", result.get(0).getTeam_name());
  }
}
