package com.rapidftr.view;

import com.rapidftr.forms.FormSection;
import com.rapidftr.model.BaseModel;

public interface FormSectionView  {

    public void initialize(FormSection formSection, BaseModel model);

    public void setEnabled(boolean enabled);

}
