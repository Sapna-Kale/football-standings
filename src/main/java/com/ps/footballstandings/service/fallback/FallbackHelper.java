package com.ps.footballstandings.service.fallback;

import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import java.util.Collections;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class FallbackHelper {

  private final CacheManager cacheManager;
  private final FallbackService fallbackService;

  public FallbackHelper(CacheManager cacheManager, FallbackService fallbackService) {
    this.cacheManager = cacheManager;
    this.fallbackService = fallbackService;
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> resolveFallback(String cacheName, String key, Class<T> type) {
    Cache cache = cacheManager.getCache(cacheName);
    if (cache != null) {
      List<T> cached = cache.get(key, List.class);
      if (cached != null && !cached.isEmpty()) {
        return cached;
      }
    }

    // Delegate to fallbackService based on type
    if (type.equals(Country.class)) {
      return (List<T>) fallbackService.getDefaultCountries();
    } else if (type.equals(League.class)) {
      return (List<T>) fallbackService.getDefaultLeagues();
    } else if (type.equals(Standing.class)) {
      return (List<T>) fallbackService.getDefaultStandings();
    }

    return Collections.emptyList();
  }
}
