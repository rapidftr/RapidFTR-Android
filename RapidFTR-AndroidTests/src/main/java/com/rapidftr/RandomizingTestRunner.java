package com.rapidftr;

import java.util.Collections;
import java.util.Random;
import junit.framework.TestCase;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;

public class RandomizingTestRunner extends InstrumentationTestRunner {

  private AndroidTestRunner runner;

  @Override
  protected AndroidTestRunner getAndroidTestRunner() {
    this.runner = super.getAndroidTestRunner();
    return runner;
  }

  @Override
  public void onStart() {
    Collections.shuffle(runner.getTestCases(), new Random(System.currentTimeMillis()));
    super.onStart();
  }

}
