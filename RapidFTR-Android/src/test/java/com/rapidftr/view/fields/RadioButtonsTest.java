package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one"));}});
        view.initialize(field, child);

        RadioButton button = (RadioButton) view.getRadioGroup().findViewWithTag("one");
        assertThat(button.getText().toString(), equalTo("one"));
        assertThat(button.isChecked(), equalTo(false));
    }

    @Test
    public void testCreateMultipleOptions() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one", "two", "three"));}});
        view.initialize(field, child);

        assertThat(view.getRadioGroup().getChildCount(), equalTo(3));
        assertNotNull(view.getRadioGroup().findViewWithTag("one"));
        assertNotNull(view.getRadioGroup().findViewWithTag("two"));
        assertNotNull(view.getRadioGroup().findViewWithTag("three"));
    }

    @Test
    public void testCheckRadioButtonToStoreValueInChildJSONArray() throws JSONException {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one", "two", "three"));}});
        view.initialize(field, child);

        RadioButton button1 = (RadioButton) view.getRadioGroup().findViewWithTag("one");
        button1.performClick();
        assertEquals("one" , child.get(field.getId()));
        RadioButton button3 = (RadioButton) view.getRadioGroup().findViewWithTag("three");
        RadioButton button2 = (RadioButton) view.getRadioGroup().findViewWithTag("two");
        button3.performClick();
        button2.performClick();
        assertEquals("two",child.get(field.getId()));
    }



}
