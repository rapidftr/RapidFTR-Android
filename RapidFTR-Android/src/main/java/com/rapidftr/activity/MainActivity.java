package com.rapidftr.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.rapidftr.R;

public class MainActivity extends RapidFtrActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivityOn(R.id.register_child_button, RegisterChildActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButtons();
    }

    private void updateButtons() {
        findViewById(R.id.register_child_button).setEnabled(getContext().isLoggedIn());

        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setText(getContext().isLoggedIn() ? R.string.log_out : R.string.log_in);

        if (getContext().isLoggedIn()) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    makeToast(R.string.logout_successful);
                    getContext().setLoggedIn(false);
                    updateButtons();
                }
            });
        } else {
            startActivityOn(R.id.login_button, LoginActivity.class);
        }
    }

}
