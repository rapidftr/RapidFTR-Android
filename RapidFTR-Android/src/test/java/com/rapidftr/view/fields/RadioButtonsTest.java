package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class RadioButtonsTest extends BaseViewSpec<RadioButtons> {

    @Before
    public void setUp() {
        view = (RadioButtons) LayoutInflater.from(new Activity()).inflate(R.layout.form_radio_button, null);
    }

    @Test
    public void testCreateSingleOption() {
        field.setOptionStrings(Arrays.asList("one"));
        view.setFormField(field);

        RadioButton button = (RadioButton) view.getRadioGroup().findViewWithTag("one");
        assertThat(button.getText().toString(), equalTo("one"));
        assertThat(button.isChecked(), equalTo(false));
    }

    @Test
    public void testCreateMultipleOptions() {
        field.setOptionStrings(Arrays.asList("one", "two", "three"));
        view.setFormField(field);

        assertThat(view.getRadioGroup().getChildCount(), equalTo(3));
        assertNotNull(view.getRadioGroup().findViewWithTag("one"));
        assertNotNull(view.getRadioGroup().findViewWithTag("two"));
        assertNotNull(view.getRadioGroup().findViewWithTag("three"));
    }

}
