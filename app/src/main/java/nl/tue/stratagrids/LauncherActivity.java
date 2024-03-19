package nl.tue.stratagrids;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import nl.tue.stratagrids.ui.login.LoginActivity;

/**
 * Activity that opens when you open the app, which will proceed to the required activity
 * based on if the user is logged in or not.
 * This uses a splashscreen for a layout but this is never visible.
 */
public class LauncherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            // Start home activity
            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
        } else {
            // No user is signed in
            // start login activity
            startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
        }

        // close splash activity
        finish();
    }
}