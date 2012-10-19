package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class DateFieldTest extends BaseViewSpec<DateField> {

    @Before
    public void setUp() {
        view = (DateField) LayoutInflater.from(new Activity()).inflate(R.layout.form_date_field, null);
    }

    @Test
    public void testNotEditable() {
        assertThat(view.getEditTextView().getInputType(), equalTo(0));
    }

    @Test
    public void testNormalTextFieldFunctionalityAvailable() {
        assertTrue(view instanceof TextField);
    }

    @Test
    public void testDateToStringConversion() {
        Calendar date = Calendar.getInstance();
        String dateText = view.getDateFormatter().format(date.getTime());
        view.setDate(date);
        assertThat(view.getText(), equalTo(dateText));
    }

    @Test
    public void testNullDateWhenTextIsEmpty() {
        view.setText("");
        assertNull(view.getDate());
    }

    @Test
    public void testNullDateWhenTextIsInvalid() {
        view.setText("Is this a date?");
        assertNull(view.getDate());
    }

    @Test
    public void testStringToDateConversion() {
        Calendar date = new GregorianCalendar(2012, 12, 31);
        view.setText(view.getDateFormatter().format(date.getTime()));
        assertThat(view.getDate(), equalTo(date));
    }

    @Test
    public void testDatePickerCallback() {
        Calendar date = new GregorianCalendar(2012, 12, 31);
        view.onDateSet(null, 2012, 12, 31);
        assertThat(view.getDate(), equalTo(date));
    }

    @Test
    public void testClearButton() {
        view.setDate(new GregorianCalendar(2012, 01, 01));
        view.onClick(null, 0);
        assertThat(view.getText(), equalTo(""));
    }

}
