package com.rapidftr.utils;

import com.rapidftr.CustomTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

@RunWith(CustomTestRunner.class)
public class CaptureHelperTest {

    CaptureHelper helper;

    @Before
    public void setUp() {
        helper = new CaptureHelper();
    }

    @Test
    public void testCaptureDirUnderRapidTRNoMedia() {
        String path = helper.getCaptureDir().getAbsolutePath();
        assertThat(path, endsWith("/rapidftr/.nomedia"));
    }

    @Test
    public void testCatureFileUnderCaptureDir() {
        String path = helper.getCaptureDir().getAbsolutePath();
        String file = helper.getTempCaptureFile().getAbsolutePath();
        assertThat(file, startsWith(path));
    }

}
