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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText mInputUsername;
    private EditText mInputUserEmail;
    private EditText mInputUserPassword;
    private Button mBtnCreateAccount;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFireBaseAuth;
    private CollectionReference mCollectionReference;
    public static final String USER_NAME = "username";
    public static final String USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initViewReferences();

        // getting the instance of the FireBaseAuth object
        mFireBaseAuth = FirebaseAuth.getInstance();

        mBtnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableButton();
                mProgressBar.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mInputUsername.getText().toString().trim())
                        && !TextUtils.isEmpty(mInputUserEmail.getText().toString().trim())
                        && !TextUtils.isEmpty(mInputUserPassword.getText().toString().trim())) {

                    String username = mInputUsername.getText().toString().trim();
                    String email = mInputUserEmail.getText().toString().trim();
                    String password = mInputUserPassword.getText().toString().trim();

                    createUserAccount(email, password, username);
                } else {
                    enableButton();
                    Snackbar.make(view, getString(R.string.snackbar_for_empty_fields), Snackbar.LENGTH_SHORT).
                            setBackgroundTint(Color.WHITE).show();
                }
            }
        });
    }

    /**
     * This method will get the reference of the views from the layout file.
     */
    public void initViewReferences() {
        mInputUsername = findViewById(R.id.edt_username);
        mInputUserEmail = findViewById(R.id.edt_login_id);
        mInputUserPassword = findViewById(R.id.edt_login_password);
        mBtnCreateAccount = findViewById(R.id.btn_create_account);
        mProgressBar = findViewById(R.id.progress_circular);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        mCollectionReference = database.collection("Users");
    }


    /**
     * This method will create the account of the user in fireBase.
     *
     * @param email    email entered by the user
     * @param password password entered by the user
     * @param username username entered by the user.
     */
    private void createUserAccount(final String email, final String password, final String username) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) {
            mFireBaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                //Login Success
                                //Log.d("currentUser", "onComplete: "+mFireBaseAuth.getCurrentUser().getUid());

                                //Getting the current User Id.
                                String currentUser = Objects.requireNonNull(mFireBaseAuth.getCurrentUser()).getUid();

                                final Map<String, Object> userObj = new HashMap<>();
                                userObj.put(USER_NAME, username);
                                userObj.put(USER_ID, currentUser);


                                mCollectionReference.document(currentUser)
                                        .set(userObj).addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent addTaskActivity = new Intent(CreateAccountActivity.this
                                                        , AddTaskActivity.class);
                                                startActivity(addTaskActivity);
                                                finish();
                                                Toast.makeText(CreateAccountActivity.this,
                                                        R.string.account_created_successfully, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                enableButton();
                                Toast.makeText(CreateAccountActivity.this, Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * This method will disable the buttons.
     */
    private void disableButton() {
        mBtnCreateAccount.setClickable(false);
    }

    /**
     * This method will enable the buttons.
     */
    private void enableButton() {
        mBtnCreateAccount.setClickable(true);
    }
}
