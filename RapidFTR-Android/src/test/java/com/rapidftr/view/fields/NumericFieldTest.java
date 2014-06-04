package com.rapidftr.view.fields;

import android.app.Activity;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(CustomTestRunner.class)
public class NumericFieldTest extends BaseViewSpec<NumericField> {

    @Before
    public void setUp() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        view = (NumericField) activity.getLayoutInflater().inflate(R.layout.form_numeric_field, null);
    }

    @Test
    public void testInheritTextFieldBehavior() {
        assertThat(view, instanceOf(TextField.class));
    }

    @Test
    public void testShouldStoreNumericDataInChildJSONObject() {
        view.initialize(field, child);
        view.setText(24234324);
        assertThat(Integer.parseInt(view.getText()), equalTo(24234324));
    }

}
