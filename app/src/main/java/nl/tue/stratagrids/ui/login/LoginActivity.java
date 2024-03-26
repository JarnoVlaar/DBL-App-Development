package nl.tue.stratagrids.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

import nl.tue.stratagrids.MainActivity;
import nl.tue.stratagrids.R;

public class LoginActivity extends AppCompatActivity{
    EditText mEmail;
    EditText mPassword;

    Button mLoginBtn;
    Button mSignUpBtn;
    Button mOfflinePlayBtn;
    Button mForgotPasswordBtn;

    FirebaseAuth fAuth;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.loginButton);
        mSignUpBtn = findViewById(R.id.registerNow);
        mOfflinePlayBtn = findViewById(R.id.playOffline);
        mForgotPasswordBtn = findViewById(R.id.forgotPassword);

        fAuth = FirebaseAuth.getInstance();

        addButtonListeners();



}

private void addButtonListeners() {
    mLoginBtn.setOnClickListener(view -> {
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        // Validation
        if (!basicInputValidation(email, password)) {
            return;
        }

        // Login user with fireauth
        fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                finishAffinity();
            } else {
                // Display error if login went wrong.
                if(!task.isSuccessful()) {
                    String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();

                    Log.d(TAG, Objects.requireNonNull(task.getException().getMessage()));

                    displayError(errorCode);


                }
            }
        });
    });


    mSignUpBtn.setOnClickListener(view -> {
        Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signUpIntent);
    });

    mOfflinePlayBtn.setOnClickListener(view -> {
        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    });

    mForgotPasswordBtn.setOnClickListener(view -> {
        Intent signUpIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(signUpIntent);
    });

    // Makes it so pressing 'finish' on the password field automatically performs login
    mPassword.setOnEditorActionListener((v, actionId, event) -> {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            mLoginBtn.performClick();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            handled = true;
        }
        return handled;
    });

}

private boolean basicInputValidation(String email, String password) {
    if (TextUtils.isEmpty(email)) {
        mEmail.setError("Email is Required");
        return false;
    }
    if (TextUtils.isEmpty(password)) {
        mPassword.setError("Password is Required");
        return false;
    }
    if (password.length() < 6) {
        mPassword.setError("Password needs to be at least 6 characters");
        return false;
    }
    return true;
}
private void displayError(@NonNull String errorCode) {
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
        default:
            Toast.makeText(LoginActivity.this, R.string.generic_error, Toast.LENGTH_LONG).show();
            break;
    }
}

}
