package com.rapidftr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

public abstract class PotentialMatchesFormSectionView extends LinearLayout implements FormSectionView {

    public PotentialMatchesFormSectionView(Context context) {
        super(context);
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

            @Cleanup PotentialMatchRepository potentialMatchRepository = RapidFtrApplication.getApplicationInstance().getBean(PotentialMatchRepository.class);
            @Cleanup ChildRepository childRepository = RapidFtrApplication.getApplicationInstance().getBean(ChildRepository.class);
            @Cleanup EnquiryRepository enquiryRepository = RapidFtrApplication.getApplicationInstance().getBean(EnquiryRepository.class);

            try {
                List<BaseModel> confirmedMatches = model.getConfirmedMatchingModels(potentialMatchRepository, childRepository, enquiryRepository);
                List<BaseModel> potentialMatches = model.getPotentialMatchingModels(potentialMatchRepository, childRepository, enquiryRepository);
                getContainer().removeAllViews();
                getContainer().addView(createPotentialMatchView(potentialMatches, confirmedMatches));
            } catch (JSONException e) {
                Log.e(null, null, e);
            }
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

    private View createPotentialMatchView(List<BaseModel> models, List<BaseModel> confirmedModels) {
        List<BaseModel> combinedModels = combineModels(models, confirmedModels);
        HighlightedFieldsViewAdapter highlightedFieldsViewAdapter =
                getHighlightedFieldsViewAdapter(combinedModels, confirmedModels);
        ListView listView = (ListView) LayoutInflater.from(getContext()).inflate(R.layout.child_list, null);
        if (combinedModels.isEmpty()) {
            listView.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.no_child_view, null));
        }
        listView.setAdapter(highlightedFieldsViewAdapter);

        return listView;
    }

    private List<BaseModel> combineModels(List<BaseModel> models, List<BaseModel> confirmedModels) {
        List<BaseModel> combinedModels = new ArrayList<BaseModel>();
        combinedModels.addAll(confirmedModels);
        combinedModels.addAll(models);
        return combinedModels;
    }

    abstract protected HighlightedFieldsViewAdapter getHighlightedFieldsViewAdapter(List<BaseModel> models, List<BaseModel> confirmedModels);
}
