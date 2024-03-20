package nl.tue.stratagrids;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import nl.tue.stratagrids.ui.login.LoginActivity;

import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.HashMap;
import java.util.Map;
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

        Button matchmakingButton = findViewById(R.id.MatchmakingButton);
        matchmakingButton.setOnClickListener(view -> {
            //TODO: fix making a dedicated handler and decompose into smaller functions
            if (fAuth.getCurrentUser() != null) {
                // Get an instance of Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Get the unique ID of the current user
                String userId = fAuth.getCurrentUser().getUid();

                // Get the current time
                long timestamp = System.currentTimeMillis();

                // Get the user's location
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                while (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission if it's not granted yet
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    //log that it's not granted yet
                    Log.d("Location", "Location permission not granted yet");
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                GeoPoint geoPoint = new GeoPoint(0.0, 0.0);
                if (location != null) {

                    geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                    //log succesful acess to location
                    Log.d("Location", "Location accessed successfully");
                } else {
                    Log.d("Location", "Location not accessed successfully");
                }


                // Create a new user with a first and last name
                Map<String, Object> user = new HashMap<>();
                user.put("time", timestamp);
                user.put("location", geoPoint);

                // Add a new document with a generated ID
                db.collection("matchmaking").document(userId)
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String TAG = "MainActivity";
                                Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
                                Toast.makeText(MainActivity.this, "Started matchmaking...", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String TAG = "MainActivity";
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(MainActivity.this, "Matchmaking failed", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
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