package com.ps.footballstandings.client;

import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "football-api", url = "https://apiv3.apifootball.com/")
public interface FootballApiClient {

  @GetMapping("?action=get_countries")
  List<Country> getCountries(@RequestParam("APIkey") String apiKey);

  @GetMapping("?action=get_leagues")
  List<League> getLeagues(
      @RequestParam("country_id") String countryId, @RequestParam("APIkey") String apiKey);

  @GetMapping("?action=get_standings")
  List<Standing> getStandings(
      @RequestParam("league_id") String leagueId, @RequestParam("APIkey") String apiKey);
}
