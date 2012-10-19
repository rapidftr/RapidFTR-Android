package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class TextFieldTest extends BaseViewSpec<TextField> {

    @Before
    public void setUp() {
        view = (TextField) LayoutInflater.from(new Activity()).inflate(R.layout.form_text_field, null);
    }

    @Test
    public void testText() {
        view.setText("sample");
        assertThat(view.getText(), equalTo("sample"));
    }

    @Test
    public void testDefaultValue() {
        field.setValue("sample");
        view.setFormField(field);
        assertThat(view.getText(), equalTo("sample"));
    }

}
