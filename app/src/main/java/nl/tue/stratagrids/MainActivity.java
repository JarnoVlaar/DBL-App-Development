package nl.tue.stratagrids;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import nl.tue.stratagrids.ui.login.LoginActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nl.tue.stratagrids.ui.game.LocalGameActivity;
import nl.tue.stratagrids.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    Boolean isMatchmaking = false;

    static final String TAG = "MainActivity";

    /**
     * Create the main activity
     *
     * @param savedInstanceState the saved instance state
     * @modifies this
     * @modifies the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setButtons();

        fAuth = FirebaseAuth.getInstance();

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.reyclerViewOngoing);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set empty adapter until data has finished loading
        recyclerView.setAdapter(new RecycleViewAdapter(new ArrayList<OnlineGame>(), this));


        // Listen for changes in online games
        viewModel.getOnlineGames().observe(this, onlineGames -> {
            // Update UI
            //Recyclerview test

            Log.d(TAG, "onCreate: " + Objects.requireNonNull(viewModel.getOnlineGames().getValue()).size());
            RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> customAdapter = new RecycleViewAdapter(viewModel.getOnlineGames().getValue(), this);
            recyclerView.setAdapter(customAdapter);
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


        checkIfMatchmaking();
        setButtons();
    }

    /**
     * Check if the current user is in matchmaking
     *
     * @modifies this.isMatchmaking
     */
    private void checkIfMatchmaking() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //check if there is a match on your userID
        if (fAuth.getCurrentUser() != null) {
            db.collection("matchmaking").document(fAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("DocumentSnapshot", "This user is in matchmaking!");
                        this.isMatchmaking = true;

                    } else {
                        Log.d("DocumentSnapshot", "This user is not in matchmaking!");
                        this.isMatchmaking = false;
                    }
                } else {
                    Log.d("DocumentSnapshot", "Failed with: ", task.getException());
                }
            });
        }
    }

    /**
     * Set the matchmaking button to either start or stop matchmaking
     *
     * @modifies this.isMatchmaking
     * @modifies the matchmaking button
     */
    private void setMatchmakingButton() {
        if (this.isMatchmaking) {
            setAsStopMatchmakingButton();
        } else {
            setAsStartMatchmakingButton();
        }

    }

    /**
     * Set the matchmaking button to stop matchmaking
     *
     * @modifies this.isMatchmaking
     * @modifies the matchmaking button
     * @pre this.isMatchmaking == true
     */
    private void setAsStopMatchmakingButton() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Button matchmakingButton = findViewById(R.id.MatchmakingButton);
        matchmakingButton.setText("Stop");
        matchmakingButton.setOnClickListener(view -> {
            db.collection("matchmaking").document(fAuth.getCurrentUser().getUid()).delete().addOnSuccessListener(aVoid -> {
                Log.d("DocumentSnapshot", "Matchmaking cancelled!");
                Toast.makeText(MainActivity.this, "Matchmaking cancelled!", Toast.LENGTH_SHORT).show();
                this.isMatchmaking = false;
                setMatchmakingButton();
            });
        });
    }

    /**
     * Set the matchmaking button to start matchmaking
     *
     * @modifies this.isMatchmaking
     * @modifies the matchmaking button
     * @pre this.isMatchmaking == false
     */
    private void setAsStartMatchmakingButton() {
        Button matchmakingButton = findViewById(R.id.MatchmakingButton);
        matchmakingButton.setText("Matchmaking");
        matchmakingButton.setOnClickListener(view -> {
            if (fAuth.getCurrentUser() != null) {

                // Get the user's location
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission if it's not granted yet
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    //log that it's not granted yet
                    Log.d("Location", "Location permission not granted yet");
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                uploadMatchmaking(location);
            }
        });
    }

    /**
     * Upload the matchmaking data to Firestore
     *
     * @param location the location of the user
     * @modifies Firestore
     * @modifies this.isMatchmaking
     * @modifies the matchmaking button
     * @modifies the user's document in Firestore
     * @modifies the matchmaking collection in Firestore
     * @pre current user is not null
     */
    private void uploadMatchmaking(Location location) {
        // Get an instance of Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the unique ID of the current user
        if (fAuth.getCurrentUser() == null) {
            Log.d("User", "User is not logged in");
            return;
        }
        String userId = fAuth.getCurrentUser().getUid();

        // Get the current time
        long timestamp = System.currentTimeMillis() / 1000;

        //get the latitude and longitude
        double latitude = 0;
        double longitude = 0;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            //log successful access to location
            Log.d("Location", "Location accessed successfully");
        } else {
            Log.d("Location", "Location not accessed successfully");
            return;
        }

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("time", timestamp);
        user.put("latitude", latitude);
        user.put("longitude", longitude);

        // Add a new document with a generated ID
        db.collection("matchmaking").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    String TAG = "MainActivity";
                    Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
                    Toast.makeText(MainActivity.this, "Started matchmaking...", Toast.LENGTH_SHORT).show();
                    this.isMatchmaking = true;
                    setMatchmakingButton();
                })
                .addOnFailureListener(error -> {
                    String TAG = "MainActivity";
                    Log.w(TAG, "Error adding document", error);
                    Toast.makeText(MainActivity.this, "Matchmaking failed", Toast.LENGTH_SHORT).show();
                });

    }

    /**
     * Set the buttons of the main activity
     *
     * @modifies the buttons
     */
    public void setButtons() {

        setMatchmakingButton();

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