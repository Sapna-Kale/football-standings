package com.ps.footballstandings.controller;

import com.ps.footballstandings.model.Country;
import com.ps.footballstandings.model.League;
import com.ps.footballstandings.model.Standing;
import com.ps.footballstandings.service.FootballServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Football API", description = "Fetch countries, leagues and standings")
@RestController
@RequestMapping("/api/football")
@CrossOrigin(origins = "http://localhost:4200")
public class FootballController {

  private final FootballServiceImpl footballService;

  public FootballController(FootballServiceImpl footballService) {
    this.footballService = footballService;
  }

  @GetMapping("/countries")
  public ResponseEntity<List<Country>> getCountries() {
    return ResponseEntity.ok(footballService.getCountries());
  }

  @GetMapping("/leagues")
  public ResponseEntity<List<League>> getLeaguesByCountry(@RequestParam String countryId) {
    return ResponseEntity.ok(footballService.getLeagues(countryId));
  }

  @GetMapping("/standings")
  public ResponseEntity<List<Standing>> getStandings(@RequestParam String leagueId) {
    return ResponseEntity.ok(footballService.getStandings(leagueId));
  }
}
