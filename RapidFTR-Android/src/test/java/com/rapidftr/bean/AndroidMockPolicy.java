package com.rapidftr.bean;

import android.util.Log;
import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;

public class AndroidMockPolicy implements PowerMockPolicy {
    @Override
    public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
        settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(Log.class.getName());
        settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader("android.content.*");
        settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader("android.widget.*");
        settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader("com.rapidftr.activity.*");
        settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader("com.rapidftr.bean.*");
    }

    @Override
    public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
        settings.addMethodsToSuppress(Log.class.getMethods());
    }
}
