package nl.tue.stratagrids;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import nl.tue.stratagrids.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.Button;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // TEMP Make email button logout the user
        FloatingActionButton mTempSignOutBtn = findViewById(R.id.fab);
        mTempSignOutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            }
            });

        // TEMP When main acitivity is created change the text to the username.
        // Does not persist but shows the username is being stored properly
        TextView textview = findViewById(R.id.textview_first);
        textview.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = null;
        if (navHostFragment != null) {
            navController = ((NavHostFragment) navHostFragment).getNavController();
        }


        if (navController != null) {
            return NavigationUI.navigateUp(navController, appBarConfiguration)
                    || super.onSupportNavigateUp();
        }

        return false;
    }

    void setButtons() {
        Button profileButton = (Button)findViewById(R.id.ProfileSettingsButton);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileSettingsActivity.class));
            }
        });

        Button logoutButton = (Button)findViewById(R.id.TempLogoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchIncludeLayout(false);
            }
        });

        Button loginButton = (Button)findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchIncludeLayout(true);
            }
        });
    }

    /**
     * Switch main activity between logged in and logged out.
     *
     * @param login if true, then switch to login. If false, switch to log out.
     */
    public void switchIncludeLayout(boolean login) {
        ViewFlipper vf = (ViewFlipper)findViewById(R.id.IncludeLayout);
        int id = (login) ? 0 : 1;
        vf.setDisplayedChild(id);

        Button b1 = (Button)findViewById(R.id.MatchmakingButton);
        b1.setEnabled(login);
        Button b2 = (Button)findViewById(R.id.MatchcodeButton);
        b2.setEnabled(login);
    }
}