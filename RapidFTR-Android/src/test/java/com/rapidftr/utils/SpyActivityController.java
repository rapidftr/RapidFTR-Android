package com.rapidftr.utils;

import android.app.Activity;
import org.robolectric.util.ActivityController;

import java.lang.reflect.Field;

import static org.mockito.Mockito.spy;

public class SpyActivityController<T extends Activity> extends ActivityController<T> {

    public static <E extends Activity> SpyActivityController<E> of(Class<E> activityClass) {
        return new SpyActivityController<E>(activityClass);
    }

    public SpyActivityController(Class<T> activityClass) {
        super(activityClass);
    }

    @Override
    public ActivityController<T> attach() {
        super.attach();
        try {
            overrideActivity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void overrideActivity() throws NoSuchFieldException, IllegalAccessException {
        T spyActivity = spy(get());
        Field f = getClass().getSuperclass().getDeclaredField("activity");
        f.setAccessible(true);
        f.set(this, spyActivity);
    }
}
