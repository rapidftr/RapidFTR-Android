package com.rapidftr.runner;

import android.app.Activity;
import android.os.Bundle;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.util.Modules;
import com.xtremelabs.robolectric.Robolectric;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContextScope;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings({"PMD"})
public class ActivityTestInjector<T> {
    private final Map<Class, Object> classesToBindToMocks = new HashMap<Class, Object>();
    private final Object testCase;
    private final Class<T> activityOrFragmentClass;

    public ActivityTestInjector(Object testCase, Class<T> activityOrFragmentClass) {
        this.testCase = testCase;
        this.activityOrFragmentClass = activityOrFragmentClass;
    }

    public <Type> ActivityTestInjector<T> bindInstance(Class<Type> classToMock, Type instanceOfClassToMock) {
        classesToBindToMocks.put(classToMock, instanceOfClassToMock);
        return this;
    }

    public void configureActivity() {
        bindMocksToContext();
        injectObjectsToTest(activityOrFragmentClass);
    }

    public void reset() {
        RoboGuice.util.reset();
    }

    private static class TestActivity extends RoboActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }

    private void bindMocksToContext() {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, Stage.DEVELOPMENT,
                Modules.override(new UnitTestModule()).with(new UnitTestOverrideModule()),
                RoboGuice.newDefaultRoboModule(Robolectric.application));
    }

    private void injectObjectsToTest(Class<T> activity) {
        try {
            T activityInstance = activity.newInstance();
            Injector activityInjector = RoboGuice.getInjector((Activity) activityInstance);
            activityInjector.getInstance(ContextScope.class).enter((Activity) activityInstance);

            activityInjector.injectMembers(testCase);
        } catch (Exception e) {
            throw new RuntimeException("Problem when entering scope", e);
        }
    }

    private class UnitTestOverrideModule extends AbstractModule {
        @Override
        protected void configure() {
            for (Entry<Class, Object> entry : classesToBindToMocks.entrySet()) {
                bind(entry.getKey()).toInstance(entry.getValue());
            }
        }
    }
}
