package com.stoughton.ryan.aggregit.git;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
 * Reformats the contribution lists into a generic format to be consumed easily
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitContributionDay {

  /*
   * Represents days of the current 'Git Year'
   * 0   -> first day of the current year's worth of days
   * 364 -> today
   */
  public int dayOfYear;

  public int count;
}
