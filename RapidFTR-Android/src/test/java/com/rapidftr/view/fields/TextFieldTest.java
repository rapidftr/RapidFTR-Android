package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class TextFieldTest extends BaseViewSpec<TextField> {

    @Before
    public void setUp() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        view = (TextField) activity.getLayoutInflater().inflate(R.layout.form_text_field, null);
    }

    @Test
    public void testText() {
        view.setText("sample");
        assertThat(view.getText(), equalTo("sample"));
    }

    @Test
    public void testDefaultValue() {
        field.setValue("sample");
        view.initialize(field, child);
        assertThat(view.getText(), equalTo("sample"));
    }

    @Test
    public void testShouldStoreTextValuesIntoChildJSONObject() throws JSONException {
        view.initialize(field, child);
        view.setText("some text");
        assertEquals("some text", child.get(field.getId()));
    }

}
