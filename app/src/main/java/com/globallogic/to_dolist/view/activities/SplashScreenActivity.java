package com.globallogic.to_dolist.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.globallogic.to_dolist.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (checkUserLoggedIn() != null) {
            startActivity(new Intent(SplashScreenActivity.this, AddTaskActivity.class));
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginUserActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
    }

    /**
     * This method check whether user is logged_in or not
     *
     * @return current user session
     */
    private FirebaseUser checkUserLoggedIn() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }
}
