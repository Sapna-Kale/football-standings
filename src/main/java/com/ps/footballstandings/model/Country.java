package com.ps.footballstandings.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Country {
  private String country_id;
  private String country_name;
  private String country_logo;
}
