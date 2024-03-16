package nl.tue.stratagrids;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import nl.tue.stratagrids.ui.login.LoginActivity;

public class ProfileSettingsActivity extends AppCompatActivity {

    Button backButton;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings_activity);

        backButton = findViewById(R.id.ProfileSettingsBackButton);
        logoutButton = findViewById(R.id.LogoutButton);

        TextView textview = findViewById(R.id.UsernameText);
        textview.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());

        backButton.setOnClickListener(view -> finish());

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(ProfileSettingsActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
            startActivity(new Intent(ProfileSettingsActivity.this, LoginActivity.class));

            finish();
            finishAffinity();

        });
    }
}