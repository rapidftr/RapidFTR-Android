package com.rapidftr.view;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;
import com.rapidftr.activity.CollectionActivity;
import com.rapidftr.activity.ViewChildActivity;
import com.rapidftr.activity.ViewEnquiryActivity;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.BaseModel;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.repository.EnquiryRepository;
import com.rapidftr.repository.PotentialMatchRepository;
import lombok.Cleanup;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotentialMatchesViewAdapter<T extends BaseModel> extends HighlightedFieldsViewAdapter<T> {

    private List<String> confirmedModels;

    public PotentialMatchesViewAdapter(Context context, List<T> allModels, String formName, Class<CollectionActivity> activityToLaunch) {
        super(context, allModels, formName, activityToLaunch);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        ImageView imageView = (ImageView) row.findViewById(R.id.confirmation_tick);
        if(confirmedModels.contains(uniqueIdFor(position))) {
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
        return row;
    }

    private String uniqueIdFor(int position) {
        try {
            return objects.get(position).getUniqueId();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setConfirmedModels(List<T> confirmedModels) {
        this.confirmedModels = collectUniqueIds(confirmedModels);
    }

    private List<String> collectUniqueIds(List<T> confirmedModels) {
        Iterable<String> uniqueIds = Iterables.transform(confirmedModels, new Function<T, String>() {
            @Override
            public String apply(T t) {
                try {
                    return t.getUniqueId();
                } catch (JSONException e) {
                    return null;
                }
            }
        });
        return Lists.newArrayList(Iterables.filter(uniqueIds,Predicates.notNull()));
    }

    public static class Builder {
        private BaseModel model;
        private Context context;
        private String formName;
        private Class<? extends CollectionActivity> activityToLaunch;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder forChild(BaseModel model) {
            this.model = model;
            formName = Enquiry.ENQUIRY_FORM_NAME;
            activityToLaunch = ViewEnquiryActivity.class;
            return this;
        }

        public Builder forEnquiry(BaseModel model) {
            this.model = model;
            formName = Child.CHILD_FORM_NAME;
            activityToLaunch = ViewChildActivity.class;
            return this;
        }

        public PotentialMatchesViewAdapter build() {
            @Cleanup PotentialMatchRepository potentialMatchRepository = RapidFtrApplication.getApplicationInstance().getBean(PotentialMatchRepository.class);
            @Cleanup ChildRepository childRepository = RapidFtrApplication.getApplicationInstance().getBean(ChildRepository.class);
            @Cleanup EnquiryRepository enquiryRepository = RapidFtrApplication.getApplicationInstance().getBean(EnquiryRepository.class);

            List<BaseModel> confirmedMatches = model.getConfirmedMatchingModels(potentialMatchRepository, childRepository, enquiryRepository);
            List<BaseModel> potentialMatches = new ArrayList<BaseModel>();
            try {
                potentialMatches = model.getPotentialMatchingModels(potentialMatchRepository, childRepository, enquiryRepository);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<BaseModel> allModels = new ArrayList<BaseModel>();
            allModels.addAll(confirmedMatches);
            allModels.addAll(potentialMatches);
            PotentialMatchesViewAdapter adapter = new PotentialMatchesViewAdapter(context, allModels, formName, activityToLaunch);
            adapter.setConfirmedModels(confirmedMatches);
            return adapter;
        }
    }
}
