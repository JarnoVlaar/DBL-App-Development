package nl.tue.stratagrids;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import nl.tue.stratagrids.ui.login.LoginActivity;

public class ProfileSettingsActivity extends AppCompatActivity {

    Button backButton, logoutButton;

    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings_activity);

        backButton = findViewById(R.id.ProfileSettingsBackButton);
        logoutButton = findViewById(R.id.LogoutButton);

        TextView textview = findViewById(R.id.UsernameText);
        textview.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        fAuth = FirebaseAuth.getInstance();

        // Set username
        if (fAuth.getCurrentUser() != null) {
            textview.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileSettingsActivity.this, "Logged Out", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ProfileSettingsActivity.this, LoginActivity.class));

                finish();
                finishAffinity();

            }
        });
    }
}