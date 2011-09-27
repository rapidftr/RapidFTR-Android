package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.rapidftr.R;
import com.rapidftr.RapidFtrApplication;

public class MainActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startActivityOn(R.id.register_child_button, RegisterChildActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtons();
    }

    private void updateButtons() {
        findViewById(R.id.register_child_button).setEnabled(RapidFtrApplication.isLoggedIn());

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setText(RapidFtrApplication.isLoggedIn() ? R.string.log_out : R.string.log_in);
        if (RapidFtrApplication.isLoggedIn()) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Log Out not really implemented yet!", Toast.LENGTH_SHORT).show();
                    RapidFtrApplication.setLoggedIn(false);
                    updateButtons();
                }
            });
        } else {
            startActivityOn(R.id.login_button, LoginActivity.class);
        }
    }

}
