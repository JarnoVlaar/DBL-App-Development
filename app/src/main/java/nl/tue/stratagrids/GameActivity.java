package nl.tue.stratagrids;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        // Back button functionality
        findViewById(R.id.BackButton).setOnClickListener(view -> finish());
    }
}