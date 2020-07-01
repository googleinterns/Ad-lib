package com.google.sps.data;

import java.time.ZonedDateTime;

/**
 * Class used to set reference date/time for testing and convert input timeAvailableUntil to date
 * timestamp
 */
public final class TimeHelper {

  private static ZonedDateTime dateTime;

  /** Constructor */
  public TimeHelper() {
    this(ZonedDateTime.now());
  }

  /** Constructor with manually set date */
  public TimeHelper(ZonedDateTime dateTime) {
    this.dateTime = dateTime;
  }

  /** Return today's date with time of hour:minute */
  public static ZonedDateTime getNewDateTime(int hour, int minute) {
    // Validate hour and minute
    if (hour < 0 || hour >= 24) {
      throw new IllegalArgumentException("Hours can only be 0 through 23 (inclusive).");
    }
    if (minute < 0 || minute >= 60) {
      throw new IllegalArgumentException("Minutes can only be 0 through 59 (inclusive).");
    }

    // Calculate current date but with hour:minute time
    // TODO: All times are currently today, wrap around times?
    return dateTime.withHour(hour).withMinute(minute).withNano(0);
  }
}
