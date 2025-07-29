package com.ps.footballstandings.service.fallback;

import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FallbackService {

  public List<Country> getDefaultCountries() {
    return List.of(
        new Country(
            "44", "England", "https://apiv3.apifootball.com/badges/logo_country/44_england.png"));
  }

  public List<League> getDefaultLeagues() {
    return List.of(
        new League(
            "44",
            "England",
            "149",
            "Non League Premier",
            "2024/2025",
            "https://apiv3.apifootball.com/badges/logo_leagues/149_non-league-premier.png"));
  }

  public List<Standing> getDefaultStandings() {
    return List.of(
        new Standing(
            "England",
            "149",
            "Non League Premier",
            "3035",
            "Horsham",
            "Promotion",
            "https://apiv3.apifootball.com/badges/3035_horsham.jpg"));
  }
}
