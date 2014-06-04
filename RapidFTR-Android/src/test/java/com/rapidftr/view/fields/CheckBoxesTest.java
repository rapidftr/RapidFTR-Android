package com.rapidftr.view.fields;

import android.app.Activity;
import android.widget.CheckBox;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class CheckBoxesTest extends BaseViewSpec<CheckBoxes> {

    @Before
    public void setUp() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        view = spy((CheckBoxes) activity.getLayoutInflater().inflate(R.layout.form_check_boxes, null));
    }

    @Test
    public void testCreateSingleCheckBox() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one"));}});
        view.initialize(field, child);

        CheckBox box = (CheckBox) view.getCheckBoxGroup().getChildAt(0);
        assertThat(box.getText().toString(), equalTo("one"));
        assertThat(box.isChecked(), equalTo(false));
    }

    @Test
    public void testCreateMultipleCheckBoxes() {
        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one", "two", "three"));}});
        view.initialize(field, child);

        assertThat(view.getCheckBoxGroup().getChildCount(), equalTo(3));
        assertNotNull(view.getCheckBoxGroup().findViewWithTag("one"));
        assertNotNull(view.getCheckBoxGroup().findViewWithTag("two"));
        assertNotNull(view.getCheckBoxGroup().findViewWithTag("three"));
    }

    @Test
    public void testStoreCheckedValueIntoChildJSONObject() throws JSONException {

        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one", "two", "three"));}});
        view.initialize(field, child);

        CheckBox checkBox1 = checkCheckBoxAtIndex(0, true);
        JSONArray options = new JSONArray();
        options.put(checkBox1.getText());
        assertEquals(options.toString(), (child.get(field.getId())).toString());

        CheckBox checkBox2 = checkCheckBoxAtIndex(1, true);
        options.put(checkBox2.getText());
        assertEquals(options.toString(), child.get(field.getId()).toString());
    }

    @Test
    public void testRemoveUncheckedCheckBoxValuesFromChildJSONObject() throws JSONException {

        field.setOptionStrings(new HashMap<String, List<String>>(){{put("en", Arrays.asList("one", "two", "three"));}});
        view.initialize(field, child);

        CheckBox checkBox1 = checkCheckBoxAtIndex(0, true);
        JSONArray options = new JSONArray();
        options.put(checkBox1.getText());
        assertEquals(options.toString(), (child.get(field.getId())).toString());

        CheckBox checkBox2 = checkCheckBoxAtIndex(1, true);
        options.put(checkBox2.getText());
        assertEquals(options.toString(), child.get(field.getId()).toString());

        checkBox1.setChecked(false);
        assertEquals("two", ((JSONArray)child.get(field.getId())).get(0));
    }


    private CheckBox checkCheckBoxAtIndex(int index, boolean checked) {
        CheckBox checkBox2 = ((CheckBox)view.getCheckBoxGroup().getChildAt(index));
        checkBox2.setChecked(checked);
        return checkBox2;
    }

}
