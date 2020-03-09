package com.globallogic.to_dolist.view.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.globallogic.to_dolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginUserActivity extends AppCompatActivity {

    private EditText mInputUserEmail;
    private EditText mInputUserPassword;
    private Button mLoginBtn;
    private Button mCreateAccountBtn;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFireBaseAuth;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViewsReferences();

        mFireBaseAuth = FirebaseAuth.getInstance();
        if(mFireBaseAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(LoginUserActivity.this, AddTaskActivity.class));
            finish();
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                disableButtons();
                if (!TextUtils.isEmpty(mInputUserEmail.getText().toString().trim())
                        && !TextUtils.isEmpty(mInputUserPassword.getText().toString().trim())) {

                    String email = mInputUserEmail.getText().toString();
                    String password = mInputUserPassword.getText().toString();

                    loginUser(email, password);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    enableButtons();
                    Snackbar.make(view, "Empty Fields Not Allowed", Snackbar.LENGTH_SHORT).setBackgroundTint(Color.WHITE).show();
                }
            }
        });

        mCreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginUserActivity.this, CreateAccountActivity.class));
            }
        });
    }

    /**
     * This method will get the reference of the views from the layout file.
     */
    private void initViewsReferences() {
        mInputUserEmail = findViewById(R.id.edt_login_id);
        mInputUserPassword = findViewById(R.id.edt_login_password);
        mLoginBtn = findViewById(R.id.btn_login);
        mCreateAccountBtn = findViewById(R.id.btn_create_account);
        mProgressBar = findViewById(R.id.progress_circular);
    }

    /**
     * This Method will login the user to their account.
     *
     * @param email    email entered by the user.
     * @param password password entered by the user.
     */
    private void loginUser(String email, String password) {
        mFireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent addTaskActivityIntent = new Intent(LoginUserActivity.this, AddTaskActivity.class);
                    startActivity(addTaskActivityIntent);
                    finish();
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    enableButtons();
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginUserActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method will disable the buttons.
     */
    private void disableButtons() {
        mCreateAccountBtn.setClickable(false);
        mLoginBtn.setClickable(false);
    }

    /**
     * This method will enable the buttons.
     */
    private void enableButtons() {
        mCreateAccountBtn.setClickable(true);
        mLoginBtn.setClickable(true);
    }
}
