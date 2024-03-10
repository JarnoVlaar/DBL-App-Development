package nl.tue.stratagrids.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import nl.tue.stratagrids.MainActivity;
import nl.tue.stratagrids.R;

/**
 * VERY ROUGH SKETCH FOR SIGNUP ACTIVITY
 * SHOULD NOT BE USED
 */
public class SignUpActivity extends AppCompatActivity{
    EditText mEmail, mPassword, mConfirmPassword, mUsername;
    Button mSignUpBtn;
    ImageButton mBackButton;
    CheckBox mTermsBox;
    FirebaseAuth fAuth;

    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Getting page componets
        mUsername = findViewById(R.id.EnterUserName);
        mEmail = findViewById(R.id.EnterEmail);
        mPassword = findViewById(R.id.CreatePassword);
        mConfirmPassword = findViewById(R.id.ConfirmPassword);

        mSignUpBtn = findViewById(R.id.SignUpButton);
        mBackButton = findViewById(R.id.LeaveSignInPage);

        mTermsBox = findViewById(R.id.CheckBox);

        fAuth = FirebaseAuth.getInstance();

        // Click listener for Login button
        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = mUsername.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                // Validation
                if (TextUtils.isEmpty(username)) {
                    mUsername.setError("Username is required.");
                    mUsername.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    mEmail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    mPassword.requestFocus();
                    return;
                }
                if(password.length() < 6) {
                    mPassword.setError("Password needs to be at least 6 characters");
                    mPassword.requestFocus();
                    return;
                }
                if(!password.equals(confirmPassword)) {
                    mPassword.setError("Passwords do not match");
                    mConfirmPassword.setError("Passwords do not match");
                    mConfirmPassword.requestFocus();
                    return;
                }
                if(!mTermsBox.isChecked()) {
                    mTermsBox.setError("You need to accept the Terms and Conditions and the Privacy Policy to proceed.");
                    mTermsBox.requestFocus();
                    return;
                }


                // Signup user with fireauth
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            // Add username to user
                            FirebaseUser user = fAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            finish();
                            finishAffinity();

                        } else {
                            // Display error if signup went wrong.
                            if(!task.isSuccessful()) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                Log.d(TAG,task.getException().getMessage());

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

                                }                                
                            }
                        }
                    }
                });
            }
        });

        // Back button functionality
        mBackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
            });
    }
}
