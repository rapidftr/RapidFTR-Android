package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class NumericFieldTest extends BaseViewSpec<NumericField> {

    @Before
    public void setUp() {
        view = (NumericField) LayoutInflater.from(new Activity()).inflate(R.layout.form_numeric_field, null);
    }

    @Test
    public void testInheritTextFieldBehavior() {
        assertThat(view, instanceOf(TextField.class));
    }

}
