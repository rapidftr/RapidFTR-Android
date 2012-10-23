package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class SelectBoxTest extends BaseViewSpec<SelectBox> {

    @Before
    public void setUp() {
        view = (SelectBox) LayoutInflater.from(new Activity()).inflate(R.layout.form_select_box, null);
    }

    @Test
    public void testAdapter() {
        field.setOptionStrings(Arrays.asList("one", "two", "three"));
        view.setFormField(field, null);

        assertThat(view.getSpinner().getAdapter().getCount(), equalTo(3));
        assertThat(view.getSpinner().getAdapter().getItem(0).toString(), equalTo("one"));
        assertThat(view.getSpinner().getAdapter().getItem(1).toString(), equalTo("two"));
        assertThat(view.getSpinner().getAdapter().getItem(2).toString(), equalTo("three"));
    }

}
