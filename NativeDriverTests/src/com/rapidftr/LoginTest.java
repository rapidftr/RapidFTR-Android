package com.rapidftr;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openqa.selenium.By;
import static org.junit.Assert.assertEquals;

public class LoginTest {
  private AndroidNativeDriver driver;
  private static String username_id = "username";
  private static String password_id = "password";
  private static String login_button_id = "login_button";
  private static String base_url_id = "base_url";

  private static String valid_username = "rapidftr";
  private static String valid_password = "rapidftr";


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

       enterTextByID(username_id, valid_username);
       enterTextByID(password_id, valid_password);
       clickLoginButton();

       assertEquals( "Login", driver.findElement(By.id(login_button_id)).getText());
  }

  @Test
    public void enterUserPassword(){
        String password = "PAZZWERD";

        startActivity();
        enterTextByID(password_id, password);
        Assert.assertTrue(driver.findElement(By.id(password_id)).getText().equalsIgnoreCase(password)) ;
    }

    @Test
    public void enterUrl(){
        String baseUrl =  "dev.rapidftr.com:3000";

        startActivity();
        clearTextById(base_url_id);
        enterTextByID(base_url_id, baseUrl);
        Assert.assertTrue(driver.findElement(By.id(base_url_id)).getText().equalsIgnoreCase(baseUrl)) ;

    }


    @Test
    public void enterUserName(){
        String username = "HELLO WORLD";

        startActivity();
        enterTextByID(username_id, username);
        Assert.assertTrue(driver.findElement(By.id(username_id)).getText().equalsIgnoreCase(username));
    }

    @Test
    public void attemptLoginWithoutPassword(){
        String username = "HELLO WORLD";

        startActivity();
        enterTextByID(username_id, username);
        enterTextByID(password_id, "");

        Assert.assertTrue(driver.findElement(By.id(password_id)).getText().equals(""));
        clickLoginButton();
    }

    @Test
    public void attemptLoginWithIncorrectPassword(){
        String password = "INCORRECT PASSWORD";

        startActivity();
        enterTextByID(username_id, valid_username);
        enterTextByID(password_id, password);

        clickLoginButton();
    }



//    @Test
//    public void enterCorrectLoginButBadUrl(){
//        String badUrl = "www.google.com";
//
//        startActivity();
//        enterTextByID(username_id, valid_username);
//        enterTextByID(password_id, valid_password);
//
//        clearTextById(base_url_id);
//        enterTextByID(base_url_id, badUrl);
//        Assert.assertTrue(driver.findElement(By.id(base_url_id)).getText().equalsIgnoreCase(badUrl)) ;
//
//        clickLoginButton();
//
//
//    }

    private void enterTextByID(String id, String text) {
        driver.findElement(By.id(id)).click();
        driver.findElement(By.id(id)).sendKeys(text);
    }

    private void clickLoginButton()
    {
        driver.findElement(By.id(login_button_id)).click();
    }

    private void clearTextById(String id)
    {
        driver.findElement(By.id(id)).clear();
    }


}

