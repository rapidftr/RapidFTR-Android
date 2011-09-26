package com.rapidftr.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.rapidftr.R;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Radu Muresan
 * Date: 9/23/11
 * Time: 1:17 PM
 */
public class LoginActivity extends RapidFtrActivity {

    public static final String DEFAULT_USERNAME = "rapidftr";
    public static final String DEFAULT_PASSWORD = "rapidftr";
    public static final String DEFAULT_URL = "dev.rapidftr.com:3000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ((EditText) findViewById(R.id.login_url)).setHint(DEFAULT_URL);
        ((EditText) findViewById(R.id.username)).setHint(DEFAULT_USERNAME);
        ((EditText) findViewById(R.id.password)).setHint(DEFAULT_PASSWORD);
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String loginUrl = getEditText(R.id.login_url, DEFAULT_URL);
                String username = getEditText(R.id.username, DEFAULT_USERNAME);
                String password = getEditText(R.id.password, DEFAULT_PASSWORD);
                try {
                    login(loginUrl, username, password);
                } catch (IOException e) {
                    loge(e.getMessage());
                    displayMessage("Login Failed: " + e.getMessage());
                }
            }
        });
        startActivityOn(R.id.camera_button, CameraPreviewActivity.class);
    }

    //TODO move this out to a service layer
    private void login(String loginUrl, String username, String password) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://" + loginUrl + "/sessions");
        post.addHeader("Accept", "application/json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("user_name", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        nameValuePairs.add(new BasicNameValuePair("imei", telephonyManager.getDeviceId()));
        nameValuePairs.add(new BasicNameValuePair("mobile_number", telephonyManager.getLine1Number()));
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = httpClient.execute(post);
        String message = response.getStatusLine().getStatusCode() == 201
                ? "Login Successful" : "Login Failed: " + response.getStatusLine().toString();
        displayMessage(message);
    }

    private void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private String getEditText(int resId, String defaultValue) {
        String currentValue = ((EditText) findViewById(resId)).getText().toString().trim();
        return currentValue.equals("") ? defaultValue : currentValue;
    }

}
