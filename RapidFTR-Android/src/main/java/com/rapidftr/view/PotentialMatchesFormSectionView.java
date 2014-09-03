package com.rapidftr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.ViewChildActivity;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.forms.FormSection;
import com.rapidftr.forms.PotentialMatchesFormSection;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.PotentialMatchRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.util.List;

public class PotentialMatchesFormSectionView extends FormSectionView {

    public PotentialMatchesFormSectionView(Context context) {
        super(context);
    }

    public PotentialMatchesFormSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PotentialMatchesFormSectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initialize(FormSection formSection, BaseModel model) {
        if (formSection instanceof PotentialMatchesFormSection) {
            Enquiry enquiry = (Enquiry) model;

            getLabel().setText(formSection.getLocalizedName());

            @Cleanup ChildRepository childRepository = RapidFtrApplication.getApplicationInstance().getBean(ChildRepository.class);
            @Cleanup PotentialMatchRepository potentialMatchRepository = RapidFtrApplication.getApplicationInstance().getBean(PotentialMatchRepository.class);
            try {
                List<Child> potentialMatches = enquiry.getPotentialMatches(childRepository, potentialMatchRepository);
                getContainer().addView(createPotentialMatchView(getContext(), potentialMatches));
            } catch (JSONException e) {
                Log.e(null, null, e);
            }
        }
    }

    private View createPotentialMatchView(Context context, List<Child> children) {
        HighlightedFieldsViewAdapter highlightedFieldsViewAdapter =
                new HighlightedFieldsViewAdapter(getContext(), children, Child.CHILD_FORM_NAME, ViewChildActivity.class);
        ListView childListView = (ListView) LayoutInflater.from(getContext()).inflate(R.layout.child_list, null);
        if (children.isEmpty()) {
            childListView.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.no_child_view, null));
        }
        childListView.setAdapter(highlightedFieldsViewAdapter);

        return childListView;
    }
}
