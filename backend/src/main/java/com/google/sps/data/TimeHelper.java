package com.google.sps.data;

import java.time.ZonedDateTime;

/**
 * Class used to set reference date/time for testing and convert input timeAvailableUntil to date
 * timestamp
 */
public final class TimeHelper {

  private static ZonedDateTime dateTime;

  /** Constructor */
  public TimeHelper(ZonedDateTime dateTime) {
    this.dateTime = dateTime;
  }

  /** Return today's date with time of hour:minute */
  public static ZonedDateTime getNewTimeToday(int hour, int minute) {
    // Calculate current date but with hour:minute time
    // TODO: All times are currently today, wrap around times?
    return dateTime.withHour(hour).withMinute(minute).withNano(0);
  }
}
