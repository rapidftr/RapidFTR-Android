package com.rapidftr.test;

import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;
import com.rapidftr.activity.test.LoginActivityIntegrationTest;
import junit.framework.TestSuite;

public class TestRunner extends InstrumentationTestRunner{

    @Override
    public TestSuite getAllTests() {
        InstrumentationTestSuite testSuite = new InstrumentationTestSuite(this);

        testSuite.addTestSuite(LoginActivityIntegrationTest.class);
        return testSuite;
    }

    @Override
    public ClassLoader getLoader() {
        return TestRunner.class.getClassLoader();
    }
}
