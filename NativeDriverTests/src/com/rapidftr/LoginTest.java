package com.rapidftr;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
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

  @Test
    public void enterUserPassword(){
        startActivity();
       // enterTextByID("password", "PAZZWERD");
       driver.findElement(By.id("password")).click();
           driver.findElement(By.id("password")).sendKeys("PAZZWERD");
        Assert.assertTrue(driver.findElement(By.id("password")).getText().equalsIgnoreCase("PAZZWERD")) ;
    }

    @Test
    public void enterUrl(){
        startActivity();
        enterTextByID("login_url", "dev.rapidftr.com:3000");
        Assert.assertTrue(driver.findElement(By.id("login_url")).getText().equalsIgnoreCase("dev.rapidftr.com:3000")) ;

    }


    @Test
    public void enterUserName(){
     startActivity();
     enterTextByID("username", "HELLO WORLD");
     Assert.assertTrue(driver.findElement(By.id("username")).getText().equalsIgnoreCase("HELLO WORLD"));


   }

    @Test
    public void attemptLoginWithoutPassword(){
     startActivity();
      enterTextByID("username", "rapiftr");

     Assert.assertTrue(driver.findElement(By.id("username")).getText().equalsIgnoreCase("HELLO WORLD"));
        enterTextByID("password", "");

     Assert.assertTrue(driver.findElement(By.id("password")).getText().equals(""));
     driver.findElement(By.id("login_button")).click();
    }

    @Test
    public void attemptLoginWithIncorrectPassword(){
        startActivity();
     enterTextByID("username", "rapiftr");
     Assert.assertTrue(driver.findElement(By.id("username")).getText().equalsIgnoreCase("HELLO WORLD"));
     enterTextByID("password", "INCORRECT PASSWORD");
     Assert.assertTrue(driver.findElement(By.id("password")).getText().equals("INCORRECT PASSWORD"));
     driver.findElement(By.id("login_button")).click();

    }



    @Test
    public void enterCorrectLoginButBadUrl(){
        startActivity();
             enterTextByID("username", "rapiftr");
           Assert.assertTrue(driver.findElement(By.id("username")).getText().equalsIgnoreCase("HELLO WORLD"));
             enterTextByID("password", "rapiftr");
           Assert.assertTrue(driver.findElement(By.id("password")).getText().equals("rapidftr"));
          enterTextByID("login_url", "www.google.com");
        Assert.assertTrue(driver.findElement(By.id("password")).getText().equalsIgnoreCase("google.com")) ;

           driver.findElement(By.id("login_button")).click();


    }

    private void enterTextByID(String id, String text) {
           driver.findElement(By.id(id)).click();
           driver.findElement(By.id(id)).sendKeys(text);
       }


}

/**

Test
  public void shouldLogIn() throws InterruptedException{
    startActivity();
         Thread.sleep(2000);
    driver.findElement(By.id("username")).click();
    driver.findElement(By.id("username")).sendKeys("rapidftr");
      Thread.sleep(2000);
    driver.findElement(By.id("password")).click();
    driver.findElement(By.id("password")).sendKeys("rapidftr");
    driver.findElement(By.id("login_button")).click();

    assertEquals("Result", "Login", driver.findElement(By.id("login_button")).getText());
  }

**/
