package com.ps.footballstandings.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Standing {
  private String country_name;
  private String league_id;
  private String league_name;
  private String team_id;
  private String team_name;
  private String overall_league_position;
  private String team_badge;
}
