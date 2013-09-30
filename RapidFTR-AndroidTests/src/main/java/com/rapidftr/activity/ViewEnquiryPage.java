package com.rapidftr.activity;

import android.widget.EditText;
import com.jayway.android.robotium.solo.Solo;
import com.rapidftr.R;
import com.rapidftr.activity.pages.EnquiryPage;
import com.rapidftr.model.Enquiry;
import com.rapidftr.view.fields.TextField;
import org.json.JSONException;

import static junit.framework.Assert.assertTrue;

public class ViewEnquiryPage extends EnquiryPage {

    public ViewEnquiryPage(Solo solo) {
        super(solo);
    }

    public void navigateToPage(String enquiryName) throws JSONException {
        solo.clickOnText("Enquiry");
        solo.clickOnText("View All");
        solo.clickOnText(enquiryName);
    }

    public void editEnquirerName(String newName) {
        solo.clickOnText("Edit");
        solo.waitForActivity(EditEnquiryActivity.class);

        TextField textField = (TextField) solo.getCurrentActivity().findViewById("enquirer_name".hashCode());
        EditText nameField = (EditText) textField.findViewById(R.id.value);

        solo.enterText(nameField, "");
        solo.enterText(nameField, newName);
    }

    public void validateData(Enquiry enquiry) throws JSONException {
        assertTrue(solo.searchText(enquiry.getShortId()));
        assertTrue(solo.searchEditText(enquiry.getEnquirerName()));
        assertTrue(solo.searchText("Edit"));
    }

}
