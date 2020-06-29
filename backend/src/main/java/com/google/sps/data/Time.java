package com.google.sps.data;

import java.util.Date;
import java.util.Calendar;

/** Class used to set reference date/time for testing and convert input timeAvailableUntil to date timestamp */
public final class Time {

  private static Date date;

  /** Constructor */
  public Time() {
    this(new Date());
  }

  /** Constructor with manually set date */
  public Time(Date date) {
    this.date = date;
  }

  /** Return today's date with time of hours:minutes in milliseconds since epoch */
  public static long getTimeMillis(int hours, int minutes) {
    // Validate hours and minutes
    if (hours < 0 || hours >= 24) {
      throw new IllegalArgumentException("Hours can only be 0 through 23 (inclusive).");
    }
    if (minutes < 0 || minutes >= 60) {
      throw new IllegalArgumentException("Minutes can only be 0 through 59 (inclusive).");
    }

    // Calculate today's date but with hours:minutes time
    // TODO: All times are currently today, wrap around times?
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.set(Calendar.HOUR_OF_DAY, hours);
    c.set(Calendar.MINUTE, minutes);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    return c.getTime().getTime();
  }
}