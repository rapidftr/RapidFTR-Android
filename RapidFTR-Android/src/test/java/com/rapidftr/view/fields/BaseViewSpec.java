package com.rapidftr.view.fields;

import com.rapidftr.forms.FormField;
import com.rapidftr.model.Child;
import junit.framework.TestCase;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public abstract class BaseViewSpec<F extends BaseView> extends TestCase {

    protected F view;
    protected FormField field;
    protected Child child;

    @Before
    public void setUpBefore() throws JSONException {
        field = new FormField();
        field.setDisplayName(new HashMap<String, String>(){{put("en", "Test Field");}});
        field.setHelpText(new HashMap<String, String>(){{put("en", "Help Field");}});
        field.setId("test_field");
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", new ArrayList<String>());}});
        child = new Child();
    }

    @Test
    public void testHaveLabel() {
        view.initialize(field, child);
        assertThat(view.getLabel().getText().toString(), equalTo(field.getLocalizedDisplayName()));
    }

    @Test
    public void testHaveHelpText() {
        view.initialize(field, child);
        assertThat(view.getHelpText().getText().toString(), equalTo(field.getLocalizedHelpText()));
    }

    @Test
    public void testShouldNotReturnNULLIfNoTranslationsAvailable() {
        Locale.setDefault(new Locale("fr"));

        view.initialize(field, child);
        assertThat(view.getLabel().getText().toString(), equalTo(""));
        Locale.setDefault(new Locale("en"));

    }

    @Test
    public void testShouldNotReturnDisplayNameForCurrentPhotoKey(){
        field.setId("current_photo_key");
        view.initialize(field, child);
        assertThat(view.getLabel().getText().toString(), equalTo(""));
    }

}
