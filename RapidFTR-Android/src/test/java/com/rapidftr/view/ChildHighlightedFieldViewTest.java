package com.rapidftr.view;


import com.google.common.io.CharStreams;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.FormField;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.FormSectionTest;
import com.rapidftr.model.Child;
import com.rapidftr.model.User;
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
            highlightedFields.put(++counter, formField);
        }

        child = new Child("1", "field_worker", loadChildDataFromClassPathResource());

        application = (RapidFtrApplication) Robolectric.getShadowApplication().getApplicationContext();
        User user = new User("userName", "password", true, "http://1.2.3.4");
        application.setCurrentUser(user);
    }

    private String loadChildDataFromClassPathResource() throws IOException {
        @Cleanup InputStream inputStream = FormSectionTest.class.getClassLoader().getResourceAsStream("child_data.json");
        return CharStreams.toString(new InputStreamReader(inputStream));
    }

    @Test
    public void shouldShowHighlightedFields() {
        HighlightedFieldViewGroup viewGroup = new HighlightedFieldViewGroup(application);
        viewGroup.prepare(child, highlightedFields);

        Iterator<Integer> iterator = highlightedFields.keySet().iterator();
        while (iterator.hasNext()) {
            assertNotNull(viewGroup.findViewById(iterator.next()));
        }
    }
}
