package nl.tue.stratagrids.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import nl.tue.stratagrids.MainActivity;
import nl.tue.stratagrids.R;

public class LoginActivity extends AppCompatActivity{
    EditText mEmail, mPassword;
    Button mLoginBtn;
    FirebaseAuth fAuth;

    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginButton);

        fAuth = FirebaseAuth.getInstance();

        // Click listener for Login button
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is Required");
                    return;
                }
                if(password.length() < 6) {
                    mPassword.setError("Password needs to be at least 6 characters");
                    return;
                }

                // Login user with fireauth
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            // Display error if login went wrong.
                            if(!task.isSuccessful()) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                Log.d(TAG,task.getException().getMessage());

                                switch (errorCode) {
                                    case "ERROR_INVALID_CUSTOM_TOKEN":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_INVALID_CUSTOM_TOKEN, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_CUSTOM_TOKEN_MISMATCH, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_CREDENTIAL":
                                        mEmail.setError(getResources().getString(R.string.ERROR_INVALID_CREDENTIAL));
                                        mEmail.requestFocus();
                                        break;

                                    case "ERROR_INVALID_EMAIL":
                                        mEmail.setError(getResources().getString(R.string.ERROR_INVALID_EMAIL));
                                        mEmail.requestFocus();
                                        break;

                                    case "ERROR_WRONG_PASSWORD":
                                        mPassword.setError(getResources().getString(R.string.ERROR_WRONG_PASSWORD));
                                        mPassword.requestFocus();
                                        mPassword.setText("");
                                        break;

                                    case "ERROR_USER_MISMATCH":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_USER_MISMATCH, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_REQUIRES_RECENT_LOGIN":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_REQUIRES_RECENT_LOGIN, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        mEmail.setError(getResources().getString(R.string.ERROR_EMAIL_ALREADY_IN_USE));
                                        mEmail.requestFocus();
                                        break;

                                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_CREDENTIAL_ALREADY_IN_USE, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_DISABLED":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_USER_DISABLED, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_TOKEN_EXPIRED":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_USER_TOKEN_EXPIRED, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_USER_NOT_FOUND":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_USER_NOT_FOUND, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_USER_TOKEN":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_INVALID_USER_TOKEN, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_OPERATION_NOT_ALLOWED":
                                        Toast.makeText(LoginActivity.this, R.string.ERROR_OPERATION_NOT_ALLOWED, Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_WEAK_PASSWORD":
                                        mPassword.setError(getResources().getString(R.string.ERROR_WEAK_PASSWORD));
                                        mPassword.requestFocus();
                                        break;

                                }                                
                            }
                        }
                    }
                });
            }
        });

    }
}
