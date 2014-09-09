package com.rapidftr.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
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

import java.util.List;

public abstract class PotentialMatchesFormSectionView extends FormSectionView {

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
            getLabel().setText(formSection.getLocalizedName());

            @Cleanup PotentialMatchRepository potentialMatchRepository = RapidFtrApplication.getApplicationInstance().getBean(PotentialMatchRepository.class);
            @Cleanup ChildRepository childRepository = RapidFtrApplication.getApplicationInstance().getBean(ChildRepository.class);
            @Cleanup EnquiryRepository enquiryRepository = RapidFtrApplication.getApplicationInstance().getBean(EnquiryRepository.class);

            try {
                List<BaseModel> potentialMatches = model.getPotentialMatchingModels(potentialMatchRepository, childRepository, enquiryRepository);
                getContainer().addView(createPotentialMatchView(getContext(), potentialMatches));
            } catch (JSONException e) {
                Log.e(null, null, e);
            }
        }
    }

    private View createPotentialMatchView(Context context, List<BaseModel> models) {
        HighlightedFieldsViewAdapter highlightedFieldsViewAdapter =
                getHighlightedFieldsViewAdapter(models);
        ListView listView = (ListView) LayoutInflater.from(getContext()).inflate(R.layout.child_list, null);
        if (models.isEmpty()) {
            listView.setEmptyView(LayoutInflater.from(getContext()).inflate(R.layout.no_child_view, null));
        }
        listView.setAdapter(highlightedFieldsViewAdapter);

        return listView;
    }

    abstract protected HighlightedFieldsViewAdapter getHighlightedFieldsViewAdapter(List<BaseModel> models);
}
