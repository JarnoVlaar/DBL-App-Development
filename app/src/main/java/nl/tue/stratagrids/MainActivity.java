package nl.tue.stratagrids;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import nl.tue.stratagrids.ui.login.LoginActivity;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseAuth;
import android.widget.Button;
import android.widget.ViewFlipper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setButtons();

        fAuth = FirebaseAuth.getInstance();

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
        ImageButton profileSettingsButton = findViewById(R.id.ProfileSettingsButton);
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

        ImageButton rulesButton = findViewById(R.id.RulesButton);
        rulesButton.setOnClickListener(this::onButtonShowRulesPopupWindowClick);

        Button localGameButton = findViewById(R.id.LocalGameButton);
        localGameButton.setOnClickListener(view -> {
            Intent signUpIntent = new Intent(MainActivity.this, GameActivity.class);
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

    /**
     * Create the popup window for the rules
     *
     * @param view the current view
     */
    public void onButtonShowRulesPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View popupView = inflater.inflate(R.layout.rules_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            v.performClick();
            return true;
        });
    }
}