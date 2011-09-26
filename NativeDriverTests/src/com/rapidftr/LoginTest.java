package com.rapidftr;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

public class LoginTest {
  private AndroidNativeDriver driver;

  @Before
  public void setUp() {
    driver = getDriver();
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  protected AndroidNativeDriver getDriver() {
    return new AndroidNativeDriverBuilder()
        .withDefaultServer()
        .build();
  }

  private void startActivity() {
    driver.startActivity("com.rapidftr.activity.LoginActivity");
  }

  @Test
  public void shouldLogIn() {
    startActivity();
    driver.findElement(By.id("login_button")).click();
  }


}
