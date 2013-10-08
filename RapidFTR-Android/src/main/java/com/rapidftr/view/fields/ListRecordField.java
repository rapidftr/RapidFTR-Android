package com.rapidftr.view.fields;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import com.rapidftr.R;

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
    public void setEnabled(boolean enabled) {
        enabled = false;
        super.setEnabled(enabled);

        getListRecordView().setEnabled(enabled);
        getListRecordView().setClickable(enabled);
        getListRecordView().setFocusable(enabled);
        getListRecordView().setFocusableInTouchMode(enabled);
    }


}
