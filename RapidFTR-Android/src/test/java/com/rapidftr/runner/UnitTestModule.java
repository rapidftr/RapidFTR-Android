package com.rapidftr.runner;

import com.google.inject.AbstractModule;
import com.rapidftr.service.LoginService;

import static org.mockito.Mockito.mock;

public class UnitTestModule extends AbstractModule {
    @Override
    protected void configure() {
        //bindAsMock(LoginService.class);
    }

    private <T> void bindAsMock(Class<T> clazz) {
        bind(clazz).toInstance(mock(clazz));
    }
}
