package nl.tue.stratagrids;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings_activity);

        Button backButton = findViewById(R.id.ProfileSettingsBackButton);

        backButton.setOnClickListener(this::onClick);
    }

    private void onClick(View v) {
        finish();
    }
}