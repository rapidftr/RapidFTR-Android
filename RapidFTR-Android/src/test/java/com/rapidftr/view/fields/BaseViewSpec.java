package com.rapidftr.view.fields;

import com.rapidftr.forms.FormField;
import com.rapidftr.model.Child;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public abstract class BaseViewSpec<F extends BaseView> extends TestCase {

    protected F view;
    protected FormField field;
    protected Child child;

    @Before
    public void setUpBefore() {
        field = new FormField();
        field.setDisplayName("Test Field");
        field.setHelpText("Help Field");
        field.setId("test_field");
        field.setOptionStrings(new ArrayList<String> ());
        child = new Child();
    }

    @Test
    public void testHaveLabel() {
        view.setFormField(field, child);
        assertThat(view.getLabel().getText().toString(), equalTo(field.getDisplayName()));
    }

    @Test
    public void testHaveHelpText() {
        view.setFormField(field, child);
        assertThat(view.getHelpText().getText().toString(), equalTo(field.getHelpText()));
    }

}
