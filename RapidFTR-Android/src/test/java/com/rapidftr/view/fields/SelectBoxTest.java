package com.rapidftr.view.fields;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.Spinner;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class SelectBoxTest extends BaseViewSpec<SelectBox> {

    @Before
    public void setUp() {
        view = (SelectBox) LayoutInflater.from(new Activity()).inflate(R.layout.form_select_box, null);
    }

    @Test
    public void testAdapterShouldPrependEmptyOptionForEmptyList() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", new ArrayList<String>());}});
        view.initialize(field, child);

        assertThat(view.getSpinner().getAdapter().getCount(), equalTo(1));
        assertThat(view.getSpinner().getAdapter().getItem(0).toString(), equalTo(""));
    }

    @Test
    public void testAdapterShouldPrependEmptyOptionForExistingList() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one"));}});
        view.initialize(field, child);

        assertThat(view.getSpinner().getAdapter().getCount(), equalTo(2));
        assertThat(view.getSpinner().getAdapter().getItem(0).toString(), equalTo(""));
    }

    @Test
    public void testAdapterShouldNotPrependEmptyOptionIfAlreadyEmpty() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("", "one"));}});
        view.initialize(field, child);
        assertThat(view.getSpinner().getAdapter().getCount(), equalTo(2));
    }

    @Test
    public void testAdapter() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one", "two", "three"));}});
        view.initialize(field, child);

        assertThat(view.getSpinner().getAdapter().getCount(), equalTo(4));
        assertThat(view.getSpinner().getAdapter().getItem(0).toString(), equalTo(""));
        assertThat(view.getSpinner().getAdapter().getItem(1).toString(), equalTo("one"));
        assertThat(view.getSpinner().getAdapter().getItem(2).toString(), equalTo("two"));
        assertThat(view.getSpinner().getAdapter().getItem(3).toString(), equalTo("three"));
    }

    @Test
    public void testShouldStoreSelectedValueInChildJSONObject() throws JSONException {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one", "two", "three"));}});
        view.initialize(field, child);

        Spinner spinner = view.getSpinner();
        String option1 = (String) spinner.getAdapter().getItem(1);
        spinner.setSelection(1);

        assertEquals(option1, child.get(field.getId()));

        String option2 = (String) spinner.getAdapter().getItem(2);
        spinner.setSelection(2);
        assertEquals(option2, child.get(field.getId()));
    }

    @Test
    public void testShouldSetTranslations() {
        HashMap<String, List<String>> optionStringsHash = new HashMap<String, List<String>>();
        optionStringsHash.put("en", Arrays.asList("one", "two"));
        optionStringsHash.put("fr", Arrays.asList("oneInFrench", "twoInFrench"));
        field.setOptionStrings(optionStringsHash);

        Locale.setDefault(new Locale("fr"));

        view.initialize(field, child);

        assertThat(view.getSpinner().getAdapter().getItem(1).toString(), equalTo("oneInFrench"));
        assertThat(view.getSpinner().getAdapter().getItem(2).toString(), equalTo("twoInFrench"));

        Locale.setDefault(new Locale("en"));

    }

    @Test
    public void testShouldNotReturnNULLIfOptionTranslationsNotAvailable() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one"));}});
        Locale.setDefault(new Locale("fr"));

        view.initialize(field, child);
        assertThat(view.getSpinner().getAdapter().getItem(0).toString(), equalTo(""));
        Locale.setDefault(new Locale("en"));

    }

}
