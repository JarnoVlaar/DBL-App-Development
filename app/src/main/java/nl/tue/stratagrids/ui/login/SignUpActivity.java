package nl.tue.stratagrids.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nl.tue.stratagrids.MainActivity;
import nl.tue.stratagrids.R;

/**
 * Activity supporting Signing up for an account.
 */
public class SignUpActivity extends AppCompatActivity{
    EditText mEmail;
    EditText mPassword;
    EditText mConfirmPassword;
    EditText mUsername;

    Button mSignUpBtn;
    Button mBackButton;

    CheckBox mTermsBox;

    FirebaseAuth fAuth;
    FirebaseFirestore db;

    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Getting page components
        mUsername = findViewById(R.id.EnterUserName);
        mEmail = findViewById(R.id.EnterEmail);
        mPassword = findViewById(R.id.CreatePassword);
        mConfirmPassword = findViewById(R.id.ConfirmPassword);

        mSignUpBtn = findViewById(R.id.SignUpButton);
        mBackButton = findViewById(R.id.LeaveSignInPage);

        mTermsBox = findViewById(R.id.CheckBox);

        fAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        addButtonListeners();
    }


private void addButtonListeners() {
    // Click listener for Login button
    mSignUpBtn.setOnClickListener(view -> {
        String username = mUsername.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();

        // Validation
        if (!basicInputValidation(username, email, password, confirmPassword)) {
            return;
        }

        // Signup user with fireauth
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignUpActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                updateUserProfile(username);

                // Finish this and all other activities
                finish();
                finishAffinity();

            } else {
                // Display error if signup went wrong.
                if(!task.isSuccessful()) {
                    String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                    displayError(errorCode);

                    Log.d(TAG, Objects.requireNonNull(task.getException().getMessage()));
                }
            }
        });
    });

    // Back button functionality
    mBackButton.setOnClickListener(view -> finish());
}

private void updateUserProfile(String username) {
    // Add username to user in Fireauth
    FirebaseUser user = fAuth.getCurrentUser();

    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
            .build();

    Objects.requireNonNull(user).updateProfile(profileUpdates)
            .addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    Log.d(TAG, "User profile updated.");
                }
            });

    // Add entries to database
    Map<String, Object> userObject = new HashMap<>();
    userObject.put("Username", username);
    userObject.put("UserID", user.getUid());
    userObject.put("Wins", 0);
    userObject.put("Ties", 0);
    userObject.put("Losses", 0);

    db.collection("User Collection").document(user.getUid())
            .set(userObject)
            .addOnSuccessListener(aVoid -> Log.d(TAG, "New userdata successfully written!"))
            .addOnFailureListener(e -> Log.w(TAG, "Error writing userdata", e));
}

    private boolean basicInputValidation(String username, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Username is required.");
            mUsername.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Email is required");
            mEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Password is required");
            mPassword.requestFocus();
            return false;
        }
        if(password.length() < 6) {
            mPassword.setError("Password needs to be at least 6 characters");
            mPassword.requestFocus();
            return false;
        }
        if(!password.equals(confirmPassword)) {
            mPassword.setError("Passwords do not match");
            mConfirmPassword.setError("Passwords do not match");
            mConfirmPassword.requestFocus();
            return false;
        }
        if(!mTermsBox.isChecked()) {
            mTermsBox.setError("You need to accept the Terms and Conditions and the Privacy Policy to proceed.");
            mTermsBox.requestFocus();
            return false;
        }
        return true;
    }

    private void displayError(@NonNull String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_INVALID_CUSTOM_TOKEN, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_CUSTOM_TOKEN_MISMATCH, Toast.LENGTH_LONG).show();
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
                Toast.makeText(SignUpActivity.this, R.string.ERROR_USER_MISMATCH, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_REQUIRES_RECENT_LOGIN, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                mEmail.setError(getResources().getString(R.string.ERROR_EMAIL_ALREADY_IN_USE));
                mEmail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_CREDENTIAL_ALREADY_IN_USE, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_USER_DISABLED, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_USER_TOKEN_EXPIRED, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_USER_NOT_FOUND, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_INVALID_USER_TOKEN, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(SignUpActivity.this, R.string.ERROR_OPERATION_NOT_ALLOWED, Toast.LENGTH_LONG).show();
                break;

            case "ERROR_WEAK_PASSWORD":
                mPassword.setError(getResources().getString(R.string.ERROR_WEAK_PASSWORD));
                mPassword.requestFocus();
                break;
            default:
                Toast.makeText(SignUpActivity.this, R.string.generic_error, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
