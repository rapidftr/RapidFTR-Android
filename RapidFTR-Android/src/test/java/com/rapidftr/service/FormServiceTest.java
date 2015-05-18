package com.rapidftr.service;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.forms.FormField;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.utils.ResourceLoader;
import com.rapidftr.utils.http.FluentRequest;
import com.rapidftr.utils.http.FluentResponse;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@PrepareForTest({FluentRequest.class, Uri.Builder.class, FormService.class})
@RunWith(PowerMockRunner.class)
public class FormServiceTest {

    private RapidFtrApplication application;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String jsonResponse = null;
    private FormService formService;
    private Resources resources;

    @Before
    public void setUp() throws IOException {
        application = mock(RapidFtrApplication.class);
        resources = mock(Resources.class);
        sharedPreferences = mock(SharedPreferences.class);
        editor = mock(SharedPreferences.Editor.class);

        jsonResponse = ResourceLoader.loadResourceAsStringFromClasspath("form_sections_for_children_and_enquiries.json");
        when(application.getResources()).thenReturn(resources);
        when(sharedPreferences.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.commit()).thenReturn(true);
        when(application.getSharedPreferences()).thenReturn(sharedPreferences);
        when(resources.openRawResource(anyInt())).thenReturn(ResourceLoader.loadResourceFromClasspath("form_sections_for_children_and_enquiries.json"));

        formService = new FormService(application);
    }

    @Test
    public void shouldDownloadAndSaveFormSections() throws Exception {
        FluentRequest request = Mockito.mock(FluentRequest.class);

        PowerMockito.mockStatic(FluentRequest.class);
        when(FluentRequest.http()).thenReturn(request);
        when(request.context(any(RapidFtrApplication.class))).thenReturn(request);
        when(request.path(anyString())).thenReturn(request);
        when(request.get()).thenReturn(new FluentResponse(buildResponse()));
        when(sharedPreferences.getString(FormService.FORM_SECTIONS_PREF, null)).thenReturn(jsonResponse);

        formService.downloadPublishedFormSections();

        verify(editor, times(1)).putString(eq(FormService.FORM_SECTIONS_PREF), eq(jsonResponse));
        verify(editor, times(1)).commit();
    }

    @Test
    public void shouldLoadDefaultFormSections() throws IOException {
        when(sharedPreferences.getString(FormService.FORM_SECTIONS_PREF, null)).thenReturn(null);

        int noOfChildFormSectionsBeforeDownload = 10, noOfEnquiryFormSectionsBeforeDownload = 7;
        assertEquals(noOfChildFormSectionsBeforeDownload, formService.getFormSections(Child.CHILD_FORM_NAME).size());
        assertEquals(noOfEnquiryFormSectionsBeforeDownload, formService.getFormSections(Enquiry.ENQUIRY_FORM_NAME).size());
    }

    @Test
    public void shouldReturnHighlightedFieldsForChildForm() throws IOException {
        when(sharedPreferences.getString(FormService.FORM_SECTIONS_PREF, null)).thenReturn(null);

        List<FormField> formFields = formService.getHighlightedFields(Child.CHILD_FORM_NAME);
        int expectedNoOfHighlightedFields = 4;
        assertEquals(expectedNoOfHighlightedFields, formFields.size());
    }

    private HttpResponse buildResponse() throws IOException {

        BasicHttpResponse httpResponse =
                new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "");

        httpResponse.setEntity(new StringEntity(jsonResponse, ContentType.APPLICATION_JSON));

        return httpResponse;
    }
}
