package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rapidftr.R;
import com.rapidftr.adapter.ChildViewAdapter;
import com.rapidftr.model.Child;
import com.rapidftr.model.Enquiry;
import com.rapidftr.repository.ChildRepository;
import com.rapidftr.utils.ApplicationInjector;
import org.json.JSONException;

import java.util.List;

public class ListRecordField extends BaseView {

    public ListRecordField(Context context) {
        super(context);
    }

    public ListRecordField(Context context, AttributeSet attr) {
        super(context, attr);
    }


    private ListView getListRecordView() {
        return (ListView) findViewById(R.id.list_records);
    }


    @Override
    protected void initialize() throws JSONException {
        super.initialize();
        Enquiry enquiry=(Enquiry) model;
        Injector inject = Guice.createInjector(new ApplicationInjector());

        List<Child> children = enquiry.getPotentialMatches(inject.getInstance(ChildRepository.class));
        ChildViewAdapter childViewAdapter = new ChildViewAdapter(getContext(), R.layout.row_child, children);
        ListView childListView = (ListView) findViewById(R.id.list_records);
        if (children.isEmpty()) {
            childListView.setEmptyView(findViewById(R.id.no_matches));
        } else
        childListView.setAdapter(childViewAdapter);
    }

    @Override
    public void setEnabled(boolean enabled) {
        enabled = false;
        super.setEnabled(enabled);

        getListRecordView().setEnabled(enabled);
        getListRecordView().setClickable(enabled);
        getListRecordView().setFocusable(enabled);
        getListRecordView().setFocusableInTouchMode(enabled);
    }


}
