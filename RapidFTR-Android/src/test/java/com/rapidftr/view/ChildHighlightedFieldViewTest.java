package com.rapidftr.view;


import com.google.common.io.CharStreams;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.FormSectionTest;
import com.rapidftr.model.Child;
import lombok.Cleanup;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

@RunWith(CustomTestRunner.class)
public class ChildHighlightedFieldViewTest {

    private Map<Integer, FormField> highlightedFields;
    private Child child;
    private RapidFtrApplication application;

    @Before
    public void setup() throws IOException, JSONException {
        List<FormSection> formSections = FormSectionTest.loadFormSectionsFromClassPathResource();

        List<FormField> fields = new ArrayList<FormField>();
        for (FormSection formSection : formSections) {
            fields.addAll(formSection.getOrderedHighLightedFields());
        }

        highlightedFields = new TreeMap<Integer, FormField>();
        int counter = 0;
        for (FormField formField : fields) {
            int id = ++counter + (int) System.currentTimeMillis();
            highlightedFields.put(id, formField);
        }

        child = new Child("1", "field_worker", loadChildDataFromClassPathResource());

        application = spy((RapidFtrApplication) Robolectric.getShadowApplication().getApplicationContext());
    }

    private String loadChildDataFromClassPathResource() throws IOException {
        @Cleanup InputStream inputStream = FormSectionTest.class.getClassLoader().getResourceAsStream("child_data.json");
        return CharStreams.toString(new InputStreamReader(inputStream));
    }

    @Test
    public void shouldShowHighlightedFields() {
        ChildHighlightedFieldViewGroup viewGroup = new ChildHighlightedFieldViewGroup(application);
        viewGroup.prepare(child, highlightedFields);

        Iterator<Integer> iterator = highlightedFields.keySet().iterator();
        while (iterator.hasNext()) {
            assertNotNull(viewGroup.findViewById(iterator.next()));
        }
    }
}
