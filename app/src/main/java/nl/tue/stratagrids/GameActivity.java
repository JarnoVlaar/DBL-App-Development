package nl.tue.stratagrids;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import nl.tue.stratagrids.databinding.ActivityGameBinding;

public class GameActivity extends AppCompatActivity {

    private ImageButton mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        // Get page components
        mBackButton = findViewById(R.id.BackButton);

        // Back button functionality
        mBackButton.setOnClickListener(view -> finish());
    }
}