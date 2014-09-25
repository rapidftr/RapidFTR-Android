package com.rapidftr.forms;

import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;

import java.util.Locale;

public class PotentialMatchesFormSection extends FormSection {

    public PotentialMatchesFormSection() {
        this.name.put(Locale.getDefault().getLanguage(), RapidFtrApplication.getApplicationInstance().getString(R.string.matches));
        this.helpText.put(Locale.getDefault().getLanguage(), RapidFtrApplication.getApplicationInstance().getString(R.string.matches_help_text));
        this.order = 0;
    }

}
