package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class CheckBoxesTest extends BaseViewSpec<CheckBoxes> {

    @Before
    public void setUp() {
        view = (CheckBoxes) LayoutInflater.from(new Activity()).inflate(R.layout.form_check_boxes, null);
    }

    @Test
    public void testCreateSingleCheckBox() {
        field.setOptionStrings(Arrays.asList("one"));
        view.setFormField(field, null);

        CheckBox box = (CheckBox) view.getCheckBoxGroup().getChildAt(0);
        assertThat(box.getText().toString(), equalTo("one"));
        assertThat(box.isChecked(), equalTo(false));
    }

    @Test
    public void testCreateMultipleCheckBoxes() {
        field.setOptionStrings(Arrays.asList("one", "two", "three"));
        view.setFormField(field, null);

        assertThat(view.getCheckBoxGroup().getChildCount(), equalTo(3));
        assertNotNull(view.getCheckBoxGroup().findViewWithTag("one"));
        assertNotNull(view.getCheckBoxGroup().findViewWithTag("two"));
        assertNotNull(view.getCheckBoxGroup().findViewWithTag("three"));
    }

}
