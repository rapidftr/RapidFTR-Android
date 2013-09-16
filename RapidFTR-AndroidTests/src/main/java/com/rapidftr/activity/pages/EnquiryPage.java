package com.rapidftr.activity.pages;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jayway.android.robotium.solo.RobotiumUtils;
import com.jayway.android.robotium.solo.Solo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnquiryPage {
    public Solo solo;
    public RobotiumUtils rutils;
    List automationFormData = Arrays.asList("Automation TextField name", "Automation TextArea location");

    public EnquiryPage(Solo solo) {
        this.solo = solo;
    }

    public void navigateToCreatePage() {
        solo.clickOnText("Enquiry");
        solo.waitForText("Enquiry details");
    }

    public List<String> getAllFormFields() {
        List<String> texts = new ArrayList<String>();
        ArrayList<View> views = rutils.removeInvisibleViews(solo.getViews());
        for (View v : views) {
            if (v instanceof TextView) {
                String text = ((TextView)v).getText().toString();
                texts.add(text);
            }
        }
        return texts;
    }
}
