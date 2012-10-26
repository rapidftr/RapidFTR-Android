package com.rapidftr;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;

import java.io.File;

public class CustomTestRunner extends RobolectricTestRunner {

    public CustomTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        MockitoAnnotations.initMocks(testClass);
    }

    @Override
    protected void bindShadowClasses() {
        super.bindShadowClasses();
    }

}
