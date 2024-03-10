package nl.tue.stratagrids;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.tue.stratagrids.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            // Start home activity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            // No user is signed in
            // start login activity
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        // close splash activity
        finish();
    }
}