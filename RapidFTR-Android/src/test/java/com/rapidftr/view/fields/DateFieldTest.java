package com.rapidftr.view.fields;

import android.app.Activity;
import android.text.InputType;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import com.rapidftr.activity.RegisterChildActivity;
import com.rapidftr.database.DatabaseHelper;
import com.rapidftr.database.ShadowSQLiteHelper;
import com.rapidftr.utils.TestInjectionModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class DateFieldTest extends BaseViewSpec<DateField> {

    @Before
    public void setUp() {
        TestInjectionModule module = new TestInjectionModule();
        module.addBinding(DatabaseHelper.class, ShadowSQLiteHelper.getInstance());
        TestInjectionModule.setUp(this, module);
        Activity activity = Robolectric.buildActivity(RegisterChildActivity.class).create().get();
        view = (DateField) activity.getLayoutInflater().inflate(R.layout.form_date_field, null);
    }

    @Test
    public void testNotEditable() {
        assertThat(view.getEditTextView().getInputType(), equalTo(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE));
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
        view.setDate(new GregorianCalendar(2012, 1, 1));
        view.onClick(null, 0);
        assertThat(view.getText(), equalTo(""));
    }

    @Test
    public void testShouldStoreDateInChildJSONObject() {
        view.initialize(field, child);
        view.onDateSet(null, 2012, 11, 31);
        assertThat(view.getText(), equalTo("31 Dec 2012"));
    }

}
