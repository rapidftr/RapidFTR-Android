package com.rapidftr;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CustomTestRunner extends RobolectricTestRunner {

    public CustomTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        MockitoAnnotations.initMocks(testClass);
    }

    @Override
    protected void bindShadowClasses() {
        super.bindShadowClasses();
    }

    @SuppressWarnings("unchecked")
    public static <T> T duck(final Object obj, final Class<T> t) {
        return (T) Proxy.newProxyInstance(t.getClassLoader(), new Class[]{t},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        try {
                            return obj.getClass()
                                    .getMethod(method.getName(), method.getParameterTypes())
                                    .invoke(obj, args);
                        } catch (NoSuchMethodException nsme) {
                            throw new NoSuchMethodError(nsme.getMessage());
                        } catch (InvocationTargetException ite) {
                            throw ite.getTargetException();
                        }
                    }
                });
    }

}
