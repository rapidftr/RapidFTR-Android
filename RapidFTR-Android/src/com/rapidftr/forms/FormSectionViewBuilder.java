package com.rapidftr.forms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.rapidftr.R;

import java.util.Hashtable;

public class FormSectionViewBuilder {

    private ScrollView scrollView;
    private Context context;
    private Hashtable<String, IWidgetBuilder> widgetBuilderHash;
    private LayoutInflater layoutInflater;
    private LinearLayout linearLayout;

    interface IWidgetBuilder{
        View build(FormField field);
    }

    public FormSectionViewBuilder(Context context){
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scrollView = new ScrollView(context);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
        setupWidgetBuilders();
    }

    private void setupWidgetBuilders() {
        widgetBuilderHash = new Hashtable<String, IWidgetBuilder>();
        widgetBuilderHash.put("text_box", new IWidgetBuilder() {
                                        @Override
                                        public View build(FormField field) { return buildTextBox(field);}
                             });
        widgetBuilderHash.put("textarea", new IWidgetBuilder() {
                                        @Override
                                        public View build(FormField field) { return buildTextArea(field);}
                             });
        widgetBuilderHash.put("select_box", new IWidgetBuilder() {
                                        @Override
                                        public View build(FormField field) { return buildSelectBox(field);}
                             });
        widgetBuilderHash.put("photo_upload_box", new IWidgetBuilder() {
                                        @Override
                                        public View build(FormField field) { return buildPhotoUploadBox(field);}
                             });
        widgetBuilderHash.put("audio_upload_box", new IWidgetBuilder() {
            @Override
            public View build(FormField field) {
                return buildAudioUploadBox(field);
            }
        });
    }

    public FormSectionViewBuilder with(ChildDetailsForm section){

        for (FormField field : section.getFields()){
            IWidgetBuilder builder = widgetBuilderHash.get(field.getType());
            if (builder != null)
                linearLayout.addView(builder.build(field));
        }
        return this;
    }

    public ScrollView build(){
        return scrollView;
    }

    private View buildTextBox(FormField field){
        return layoutInflater.inflate(R.layout.text_field, null);
    }
    private View buildTextArea(FormField field){
        return layoutInflater.inflate(R.layout.textarea, null);
    }
    private View buildSelectBox(FormField field){
        return layoutInflater.inflate(R.layout.select_box, null);
    }
    private View buildPhotoUploadBox(FormField field){
        return layoutInflater.inflate(R.layout.photo_upload_box, null);
    }
    private View buildAudioUploadBox(FormField field){
        return layoutInflater.inflate(R.layout.audio_upload_box, null);
    }

}
