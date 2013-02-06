package com.rapidftr.utils;


import com.rapidftr.CustomTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Calendar;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(CustomTestRunner.class)
public class RapidFtrDateTimeTest {

    @Test
    public void shouldParseDateTimeStringAndReturnCalendarObject() throws ParseException {
        String parsedDateTime = new RapidFtrDateTime(5, 2 , 2013).defaultFormat();
        Calendar calendar = RapidFtrDateTime.getDateTime(parsedDateTime);
        assertThat(calendar.get(Calendar.DATE), is(5));
        assertThat(calendar.get(Calendar.YEAR), is(2013));
        assertThat(calendar.get(Calendar.MONTH), is(Calendar.FEBRUARY));
    }
}
