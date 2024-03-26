package nl.tue.stratagrids.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

import nl.tue.stratagrids.R;

public class ForgotPasswordActivity extends AppCompatActivity{
    EditText mEmail;

    TextView mTextSuccess;

    ProgressBar mProgressBar;

    Button mSendMail;
    Button mClose;

    FirebaseAuth fAuth;

    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_password_activity);

        mEmail = findViewById(R.id.editTextEmailAddress);
        mSendMail = findViewById(R.id.buttonSendMail);
        mClose = findViewById(R.id.buttonClose);
        mProgressBar = findViewById(R.id.progressBar);
        mTextSuccess = findViewById(R.id.textSendSuccess);

        mProgressBar.setVisibility(View.INVISIBLE);
        mTextSuccess.setVisibility(View.INVISIBLE);

        fAuth = FirebaseAuth.getInstance();

        addButtonListeners();

}

private void addButtonListeners() {
    mClose.setOnClickListener(view -> finish());

    mSendMail.setOnClickListener(view -> {
        String email = mEmail.getText().toString().trim();
        mTextSuccess.setVisibility(View.INVISIBLE);

        // Validation
        if (!basicInputValidation(email)) {
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);

        // Password reset request with fireauth
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mProgressBar.setVisibility(View.INVISIBLE);
                mTextSuccess.setVisibility(View.VISIBLE);
            } else {
                // Display error if request went wrong.
                if(!task.isSuccessful()) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();

                    Log.d(TAG, Objects.requireNonNull(task.getException().getMessage()));

                    displayError(errorCode);
                }
            }
        });
    });

    }

private boolean basicInputValidation(String email) {
    if (TextUtils.isEmpty(email)) {
        mEmail.setError("Email is Required");
        return false;
    }
    return true;
}
private void displayError(@NonNull String errorCode) {
    switch (errorCode) {
        case "ERROR_INVALID_CUSTOM_TOKEN":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_INVALID_CUSTOM_TOKEN, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_CUSTOM_TOKEN_MISMATCH":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_CUSTOM_TOKEN_MISMATCH, Toast.LENGTH_LONG).show();
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
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_WRONG_PASSWORD, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_USER_MISMATCH":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_USER_MISMATCH, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_REQUIRES_RECENT_LOGIN":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_REQUIRES_RECENT_LOGIN, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_EMAIL_ALREADY_IN_USE":
            mEmail.setError(getResources().getString(R.string.ERROR_EMAIL_ALREADY_IN_USE));
            mEmail.requestFocus();
            break;

        case "ERROR_CREDENTIAL_ALREADY_IN_USE":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_CREDENTIAL_ALREADY_IN_USE, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_USER_DISABLED":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_USER_DISABLED, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_USER_TOKEN_EXPIRED":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_USER_TOKEN_EXPIRED, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_USER_NOT_FOUND":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_USER_NOT_FOUND, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_INVALID_USER_TOKEN":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_INVALID_USER_TOKEN, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_OPERATION_NOT_ALLOWED":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_OPERATION_NOT_ALLOWED, Toast.LENGTH_LONG).show();
            break;

        case "ERROR_WEAK_PASSWORD":
            Toast.makeText(ForgotPasswordActivity.this, R.string.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG).show();
            break;
        default:
            Toast.makeText(ForgotPasswordActivity.this, R.string.generic_error, Toast.LENGTH_LONG).show();
            break;
    }
}

}
