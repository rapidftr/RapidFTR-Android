package com.rapidftr.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.rapidftr.R;
import com.rapidftr.activity.CollectionActivity;
import com.rapidftr.activity.ViewChildActivity;
import com.rapidftr.activity.ViewEnquiryActivity;
import com.rapidftr.adapter.HighlightedFieldsViewAdapter;
import com.rapidftr.model.BaseModel;
import org.json.JSONException;

import javax.sql.rowset.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotentialMatchesViewAdapter<T extends BaseModel> extends HighlightedFieldsViewAdapter<T> {

    private List<String> confirmedModels;

    public PotentialMatchesViewAdapter(Context context, List<T> allModels, List<T> confirmedModels, String formName, Class<CollectionActivity> activityToLaunch) {
        super(context, allModels, formName, activityToLaunch);
        this.confirmedModels = collectUniqueIds(confirmedModels);
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
}
