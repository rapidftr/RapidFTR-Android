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
       String username = "rapidftr";
       String password = "rapidftr";

       startActivity();

       enterTextByID(username_id, username);
       enterTextByID(password_id, password);
       driver.findElement(By.id(login_button_id)).click();

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
        startActivity();
        enterTextByID(base_url_id, "dev.rapidftr.com:3000");
        Assert.assertTrue(driver.findElement(By.id(base_url_id)).getText().equalsIgnoreCase("dev.rapidftr.com:3000")) ;

    }


    @Test
    public void enterUserName(){
        startActivity();
        enterTextByID(username_id, "HELLO WORLD");
        Assert.assertTrue(driver.findElement(By.id(username_id)).getText().equalsIgnoreCase("HELLO WORLD"));
    }

    @Test
    public void attemptLoginWithoutPassword(){
        startActivity();
        enterTextByID(username_id, "rapiftr");

        Assert.assertTrue(driver.findElement(By.id(username_id)).getText().equalsIgnoreCase("HELLO WORLD"));
        enterTextByID(password_id, "");

        Assert.assertTrue(driver.findElement(By.id(password_id)).getText().equals(""));
        driver.findElement(By.id(login_button_id)).click();
    }

    @Test
    public void attemptLoginWithIncorrectPassword(){
        startActivity();
        enterTextByID(username_id, "rapiftr");
        Assert.assertTrue(driver.findElement(By.id(username_id)).getText().equalsIgnoreCase("HELLO WORLD"));

        enterTextByID(password_id, "INCORRECT PASSWORD");
        Assert.assertTrue(driver.findElement(By.id(password_id)).getText().equals("INCORRECT PASSWORD"));
        driver.findElement(By.id(login_button_id)).click();

    }



    @Test
    public void enterCorrectLoginButBadUrl(){
        startActivity();
        enterTextByID(username_id, "rapiftr");
        Assert.assertTrue(driver.findElement(By.id(username_id)).getText().equalsIgnoreCase("HELLO WORLD"));
        enterTextByID(password_id, "rapiftr");
        Assert.assertTrue(driver.findElement(By.id(password_id)).getText().equals("rapidftr"));
        enterTextByID(base_url_id, "www.google.com");
        Assert.assertTrue(driver.findElement(By.id(password_id)).getText().equalsIgnoreCase("google.com")) ;

        driver.findElement(By.id(login_button_id)).click();


    }

    private void enterTextByID(String id, String text) {
        driver.findElement(By.id(id)).click();
        driver.findElement(By.id(id)).sendKeys(text);
    }


}

