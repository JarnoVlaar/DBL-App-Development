package nl.tue.stratagrids;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import nl.tue.stratagrids.ui.game.LocalGameActivity;
import nl.tue.stratagrids.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setButtons();

        fAuth = FirebaseAuth.getInstance();

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Listen for changes in online games
        viewModel.getOnlineGames().observe(this, onlineGames -> {
            // Update UI
        });

        // Refresh online games if user is logged in
        if (fAuth.getCurrentUser() != null) {
            viewModel.refreshOnlineGames();
        }

        // Set username to top bar if user is logged in
        if (fAuth.getCurrentUser() != null) {
            TextView textview = findViewById(R.id.UsernameText);
            textview.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            switchIncludeLayout(true);
        } else {
            switchIncludeLayout(false);
        }
    }

    void setButtons() {
        Button profileSettingsButton = findViewById(R.id.ProfileSettingsButton);
        profileSettingsButton.setOnClickListener(view -> {
            if (fAuth.getCurrentUser() != null) {
                Intent signUpIntent = new Intent(MainActivity.this, ProfileSettingsActivity.class);
                signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(signUpIntent);
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }

        });

        Button loginButton = findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        Button localGameButton = findViewById(R.id.LocalGameButton);
        localGameButton.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(MainActivity.this, LocalGameActivity.class);
            signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signUpIntent);
        });
    }

    /**
     * Switch main activity between logged in and logged out.
     *
     * @param login if true, then switch to login. If false, switch to log out.
     */
    public void switchIncludeLayout(boolean login) {
        ViewFlipper vf = findViewById(R.id.IncludeLayout);
        int id = (login) ? 0 : 1;
        vf.setDisplayedChild(id);

        Button b1 = findViewById(R.id.MatchmakingButton);
        b1.setEnabled(login);
        Button b2 = findViewById(R.id.MatchcodeButton);
        b2.setEnabled(login);
    }
}