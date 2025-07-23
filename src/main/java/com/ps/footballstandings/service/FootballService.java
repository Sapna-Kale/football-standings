package com.ps.footballstandings.service;

import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import java.util.List;

public interface FootballService {
  List<Country> getCountries();

  List<League> getLeagues(String countryId);

  List<Standing> getStandings(String leagueId);
}
