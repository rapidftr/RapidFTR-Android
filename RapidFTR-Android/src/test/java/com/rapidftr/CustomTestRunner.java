package com.rapidftr;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;

import java.io.File;

public class CustomTestRunner extends RobolectricTestRunner {


    public CustomTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
}
