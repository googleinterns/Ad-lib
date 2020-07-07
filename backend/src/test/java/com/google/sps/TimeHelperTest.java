package com.google.sps;

import com.google.sps.data.TimeHelper;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class TimeHelperTest {

  private TimeHelper timeHelper;

  @Before
  public void setUp() {
    // Set "current" date to  1/1/2020 2:00pm ET
    ZonedDateTime dateTime =
        ZonedDateTime.of(
            /* year= */ 2020,
            /* month= */ 1,
            /* date= */ 1,
            /* hour= */ 14,
            /* minute= */ 0,
            /* second= */ 0,
            /* nanosecond= */ 0,
            /* zone= */ ZoneId.of("US/Eastern"));
    timeHelper = new TimeHelper(dateTime);
  }

  @Test
  public void inputTimeToMillis() {
    // 1/1/2020 2:00pm ET
    ZonedDateTime dateTime =
        ZonedDateTime.of(
            /* year= */ 2020,
            /* month= */ 1,
            /* date= */ 1,
            /* hour= */ 14,
            /* minute= */ 0,
            /* second= */ 0,
            /* nanosecond= */ 0,
            /* zone= */ ZoneId.of("US/Eastern"));
    Assert.assertEquals(/* expected */ dateTime, /* actual */ TimeHelper.getNewTimeToday(14, 0));

    // 1/1/2020 10:30am ET
    dateTime = dateTime.withHour(10).withMinute(30);
    Assert.assertEquals(dateTime, TimeHelper.getNewTimeToday(10, 30));

    // 1/1/2020 11:45pm ET
    dateTime = dateTime.withHour(23).withMinute(45);
    Assert.assertEquals(dateTime, TimeHelper.getNewTimeToday(23, 45));
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeHours() {
    ZonedDateTime dateTime = TimeHelper.getNewTimeToday(-1, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void over24Hours() {
    ZonedDateTime dateTime = TimeHelper.getNewTimeToday(24, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeMinutes() {
    ZonedDateTime dateTime = TimeHelper.getNewTimeToday(2, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void over60Minutes() {
    ZonedDateTime dateTime = TimeHelper.getNewTimeToday(2, 60);
  }
}
