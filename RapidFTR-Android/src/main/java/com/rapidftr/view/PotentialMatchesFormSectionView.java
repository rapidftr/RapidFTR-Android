package com.rapidftr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.PotentialMatchesFormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.PotentialMatchRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PotentialMatchesFormSectionView extends LinearLayout implements FormSectionView {

    private HighlightedFieldsViewAdapter highlightedFieldsViewAdapter;

    public PotentialMatchesFormSectionView(Context context, HighlightedFieldsViewAdapter highlightedFieldsViewAdapter) {
        super(context);
        this.highlightedFieldsViewAdapter = highlightedFieldsViewAdapter;
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initializeView(context);
    }

    public PotentialMatchesFormSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public PotentialMatchesFormSectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView(context);
    }

    private void initializeView(Context context) {
        this.setOrientation(LinearLayout.VERTICAL);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.form_section, this);
    }

    @Override
    public void initialize(FormSection formSection, BaseModel model) {
        if (formSection instanceof PotentialMatchesFormSection) {
            getLabel().setText(formSection.getLocalizedName());
            getContainer().addView(createPotentialMatchView());
        }
    }

    protected LinearLayout getContainer() {
        return (LinearLayout) findViewById(R.id.container);
    }

    protected TextView getLabel() {
        return (TextView) findViewById(R.id.label);
    }

    protected TextView getHelpText() {
        return (TextView) findViewById(R.id.help_text);
    }

    private View createPotentialMatchView() {
        ListView listView = (ListView) LayoutInflater.from(getContext()).inflate(R.layout.child_list, null);
        if (highlightedFieldsViewAdapter.getCount() == 0) {
            listView.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.no_child_view, null));
        }
        listView.setAdapter(highlightedFieldsViewAdapter);

        return listView;
    }
}
