package com.google.sps;

import com.google.sps.data.Time;
import java.util.Date;
import java.util.Calendar;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** */
public final class TimeTest {

  private Time time;

  @Before
  public void setUp() {
    // Set "current" date to  1/1/2020 2:00pm GMT
    Calendar c = Calendar.getInstance();
    c.set(/* year= */ 2020, /* month= */ 0, /* date= */ 1, /* hour= */ 14, /* minute= */ 0, /* second= */ 0);
    c.set(Calendar.MILLISECOND, 0);
    Date date = c.getTime();

    time = new Time(date);
  }

  @Test
  public void inputTimeToMillis() {
    // Set year, month, date and make second and milliseconds 0.
    Calendar c = Calendar.getInstance();
    c.set(/* year= */ 2020, /* month= */ 0, /* date= */ 1);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    
    // 1/1/2020 2:00pm GMT
    c.set(Calendar.HOUR_OF_DAY, 14);
    c.set(Calendar.MINUTE, 0);
    Assert.assertEquals(/* expected */c.getTime().getTime(), /* actual */time.getTimeMillis(14, 0));

    // 1/1/2020 10:30am GMT
    c.set(Calendar.HOUR_OF_DAY, 10);
    c.set(Calendar.MINUTE, 30);
    Assert.assertEquals(c.getTime().getTime(), time.getTimeMillis(10, 30));

    // 1/1/2020 11:45pm GMT
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 45);
    Assert.assertEquals(c.getTime().getTime(), time.getTimeMillis(23, 45));
  }

  @Test (expected = IllegalArgumentException.class) 
  public void negativeHours() {
    long timestamp = time.getTimeMillis(-1, 0);
  }

  @Test (expected = IllegalArgumentException.class) 
  public void over24Hours() {
    long timestamp = time.getTimeMillis(24, 0);
  }

  @Test (expected = IllegalArgumentException.class) 
  public void negativeMinutes() {
    long timestamp = time.getTimeMillis(2, -1);
  }

  @Test (expected = IllegalArgumentException.class) 
  public void over60Minutes() {
    long timestamp = time.getTimeMillis(2, 60);
  }
}