package nl.tue.stratagrids;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import nl.tue.stratagrids.ui.login.LoginActivity;

public class ProfileSettingsActivity extends AppCompatActivity {

    Button backButton;
    Button logoutButton;
    Button deleteAccountButton;

    TextView textStatsWins;
    TextView textStatsTies;
    TextView textStatsLoss;

    FirebaseAuth fAuth;

    FirebaseFirestore db;

    private static final String TAG = "ProfileSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings_activity);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser user = fAuth.getCurrentUser();

        backButton = findViewById(R.id.ProfileSettingsBackButton);
        logoutButton = findViewById(R.id.LogoutButton);
        deleteAccountButton = findViewById(R.id.DeleteAccountButton);

        textStatsWins = findViewById(R.id.StatisticsWinsNumber);
        textStatsTies = findViewById(R.id.StatisticsTiesNumber);
        textStatsLoss = findViewById(R.id.StatisticsLossNumber);

        addButtonListeners();

        // Set username to respective textfield.
        TextView textview = findViewById(R.id.UsernameText);
        textview.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());



        // Get the wins, losses and ties for current user.
        if (user != null) {
            db.collection("User Collection")
                    // Searching for all documents with field UserID matching current users ID.
                .whereEqualTo("UserID",user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            // Get relevant fields from document and set them to appropriate textFields.
                            Long wins = document.getLong("Wins");
                            Long ties = document.getLong("Ties");
                            Long losses = document.getLong("Losses");

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

        // TODO: Ask for confirmation before deletion.
        deleteAccountButton.setOnClickListener(view -> {
            // Save user ID if user gets deleted.
            String userUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

            // Delete user from fireauth
            FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // If user was deleted, delete document from Firestore
                    db.collection("User Collection").document(userUid)
                            .delete()
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "User document successfully deleted"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error deleting user document", e));

                    // Making logs, messages and return to Login page.
                    Log.d(TAG, "User account deleted.");
                    Toast.makeText(ProfileSettingsActivity.this, "Deleted account", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ProfileSettingsActivity.this, LoginActivity.class));

                    finish();
                    finishAffinity();
                } else {
                    Log.d(TAG, "User account was not deleted.");
                    Toast.makeText(ProfileSettingsActivity.this, "Account deletion requires a recent sign-in.", Toast.LENGTH_LONG).show();
                }
            });;



        });
    }

}