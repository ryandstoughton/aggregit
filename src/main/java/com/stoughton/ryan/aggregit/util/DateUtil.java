package com.stoughton.ryan.aggregit.util;

import java.time.Instant;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {

  public LocalDate getCurrentDate() {
    String nowString = Instant.now().toString().substring(0, 10);
    return LocalDate.parse(nowString);
  }

}
