package com.rapidftr.adapter;

import android.view.View;
import android.view.ViewGroup;
import com.rapidftr.CustomTestRunner;
import com.rapidftr.forms.FormSection;
import com.rapidftr.model.Child;
import com.rapidftr.view.DefaultFormSectionView;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(CustomTestRunner.class)
public class FormSectionPagerAdapterTest {

    protected List<FormSection> formSections = CustomTestRunner.formSectionSeed;
    protected Child child = mock(Child.class);
    protected FormSectionPagerAdapter adapter = spy(new FormSectionPagerAdapter(formSections, child, true));

    @Test
    public void testCount() {
        assertThat(adapter.getCount(), equalTo(formSections.size()));
    }

    @Test
    public void testDestroyItem() {
        ViewGroup group = mock(ViewGroup.class);
        View view = mock(View.class);

        adapter.destroyItem(group, 0, view);
        verify(group).removeView(view);
    }

    @Test
    public void testInstantiateEnabledItem() {
        DefaultFormSectionView view = mock(DefaultFormSectionView.class);
        ViewGroup container = mock(ViewGroup.class);

        doReturn(view).when(adapter).createFormSectionView(container);
        adapter.instantiateItem(container, 1);

        verify(view).initialize(formSections.get(1), child);
        verify(view).setEnabled(true);
        verify(container).addView(view, 0);
    }

    @Test
    public void testInstantiateDisabledItem() {
        DefaultFormSectionView view = mock(DefaultFormSectionView.class);
        ViewGroup container = mock(ViewGroup.class);

        doReturn(view).when(adapter).createFormSectionView(container);
        adapter.editable = false;
        adapter.instantiateItem(container, 1);

        verify(view).initialize(formSections.get(1), child);
        verify(view).setEnabled(false);
        verify(container).addView(view, 0);
    }

    @Test
    public void shouldReturnViewAsKey() {
        DefaultFormSectionView view = mock(DefaultFormSectionView.class);
        doReturn(view).when(adapter).createFormSectionView(any(ViewGroup.class));

        Object actual = adapter.instantiateItem(mock(ViewGroup.class), 0);
        assertThat(actual, equalTo((Object) view));
    }

}
