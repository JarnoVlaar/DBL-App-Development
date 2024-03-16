package nl.tue.stratagrids;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import nl.tue.stratagrids.ui.login.LoginActivity;

public class ProfileSettingsActivity extends AppCompatActivity {

    Button backButton;
    Button logoutButton;

    FirebaseAuth fAuth;

    FirebaseFirestore db;

    private static final String TAG = "ProfileSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings_activity);

        db = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();

        backButton = findViewById(R.id.ProfileSettingsBackButton);
        logoutButton = findViewById(R.id.LogoutButton);

        TextView textview = findViewById(R.id.UsernameText);
        textview.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());

        TextView textStatsWins = findViewById(R.id.StatisticsWinsNumber);
        TextView textStatsTies = findViewById(R.id.StatisticsTiesNumber);
        TextView textStatsLoss = findViewById(R.id.StatisticsLossNumber);

        // Get current user token

        FirebaseUser user = fAuth.getCurrentUser();

        if (user != null) {
            Log.d(TAG, "Ewa user is niet null dat is echt mega hip");
            db.collection("User Collection")
                .whereEqualTo("UserID",user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "Taak is uberhaupt uitgevoerd dat is kaolo lijp");
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Taak is ook nog eens sucessvol volbracht, bravo");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Long wins = document.getLong("Wins");
                            Long ties = document.getLong("Ties");
                            Long losses = document.getLong("Losses");


                            // Now you have the username, you can use it as you want
                            textStatsWins.setText(String.format(String.valueOf(wins)));
                            textStatsTies.setText(String.format(String.valueOf(ties)));
                            textStatsLoss.setText(String.format(String.valueOf(losses)));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    } else {
            Log.d(TAG, "No User logged in");
        }
        addButtonListeners();
    }

    private void addButtonListeners() {
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