package com.rapidftr.model;


import com.rapidftr.CustomTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;

import static com.rapidftr.model.Child.generateUniqueId;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CustomTestRunner.class)
public class ChildTest {

    @Test
    public void testShouldGenerateUniqueId(){
        Calendar calendar = mock(Calendar.class);
        when(calendar.get(Calendar.YEAR)).thenReturn(2012);
        when(calendar.get(Calendar.MONTH)).thenReturn(10);
        when(calendar.get(Calendar.DAY_OF_MONTH)).thenReturn(24);

        String guid = generateUniqueId("rapidftr");

        assertTrue(guid.contains("rapidftr20121024"));
        assertTrue(guid.length() == 21);
    }

}
