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
    c.set(2020, 0, 1, 14, 0, 0);
    c.set(Calendar.MILLISECOND, 0);
    Date date = c.getTime();

    time = new Time(date);
  }

  @Test
  public void inputTimeToMillis() {
    // Set year, month, date and make second and milliseconds 0.
    Calendar c = Calendar.getInstance();
    c.set(2020, 0, 1);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    
    // 1/1/2020 2:00pm GMT
    c.set(Calendar.HOUR_OF_DAY, 14);
    c.set(Calendar.MINUTE, 0);
    Assert.assertEquals(/* expected */c.getTime().getTime(), /* actual */Time.getTimeMillis(14, 00));

    // 1/1/2020 10:30am GMT
    c.set(Calendar.HOUR_OF_DAY, 10);
    c.set(Calendar.MINUTE, 30);
    Assert.assertEquals(c.getTime().getTime(), Time.getTimeMillis(10, 30));

    // 1/1/2020 11:45pm GMT
    c.set(Calendar.HOUR_OF_DAY, 23);
    c.set(Calendar.MINUTE, 45);
    Assert.assertEquals(c.getTime().getTime(), Time.getTimeMillis(23, 45));
  }
}