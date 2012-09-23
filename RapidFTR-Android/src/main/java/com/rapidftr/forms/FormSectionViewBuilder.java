package com.rapidftr.forms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.rapidftr.R;

import java.util.HashMap;
import java.util.Map;

public class FormSectionViewBuilder {

    private ScrollView scrollView;
    private Context context;
    private Map<String, IWidgetBuilder> widgetBuilders;
    private LayoutInflater layoutInflater;
    private LinearLayout linearLayout;

    interface IWidgetBuilder {
        View build(FormField field);
    }

    public FormSectionViewBuilder(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scrollView = new ScrollView(context);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
        setupWidgetBuilders();
    }

    private void setupWidgetBuilders() {
        widgetBuilders = new HashMap<String, IWidgetBuilder>();
        widgetBuilders.put("text_field", new IWidgetBuilder() {
            @Override
            public View build(FormField field) {
                return buildTextBox(field);
            }
        });
        widgetBuilders.put("textarea", new IWidgetBuilder() {
            @Override
            public View build(FormField field) {
                return buildTextArea(field);
            }
        });
        widgetBuilders.put("select_box", new IWidgetBuilder() {
            @Override
            public View build(FormField field) {
                return buildSelectBox(field);
            }
        });
        widgetBuilders.put("photo_upload_box", new IWidgetBuilder() {
            @Override
            public View build(FormField field) {
                return buildPhotoUploadBox(field);
            }
        });
        widgetBuilders.put("audio_upload_box", new IWidgetBuilder() {
            @Override
            public View build(FormField field) {
                return buildAudioUploadBox(field);
            }
        });
    }

    public FormSectionViewBuilder with(ChildDetailsForm section) {

        for (FormField field : section.getFields()) {
            IWidgetBuilder builder = widgetBuilders.get(field.getType());
            if (builder != null)
                linearLayout.addView(builder.build(field));
        }
        return this;
    }

    public ScrollView build() {
        return scrollView;
    }

    private View inflateView(int view) {
        return layoutInflater.inflate(view, null);
    }

    private void setLabel(View view, String display_name) {
        ((TextView) view.findViewById(R.id.label)).setText(display_name);
    }

    private View buildTextBox(FormField field) {
        View view = inflateView(R.layout.text_field);
        setLabel(view, field.getDisplay_name());
        ((EditText) view.findViewById(R.id.value)).setText(field.getValue() != null ? field.getValue().toString() : "");
        return view;
    }

    private View buildTextArea(FormField field) {
        View view = inflateView(R.layout.textarea);
        setLabel(view, field.getDisplay_name());
        return view;
    }

    private View buildSelectBox(FormField field) {
        View view = inflateView(R.layout.select_box);
        setLabel(view, field.getDisplay_name());
        ArrayAdapter<String> optionsAdapter =
                new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, field.getOption_strings());
        ((Spinner) view.findViewById(R.id.field_options)).setAdapter(optionsAdapter);
        return view;
    }

    private View buildPhotoUploadBox(FormField field) {
        return inflateView(R.layout.photo_upload_box);
    }

    private View buildAudioUploadBox(FormField field) {
        return inflateView(R.layout.audio_upload_box);
    }

}
