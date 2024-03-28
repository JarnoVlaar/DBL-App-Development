package nl.tue.stratagrids;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<List<OnlineGame>> onlineGames;

    public MainViewModel() {
        onlineGames = new MutableLiveData<>();
    }

    public MutableLiveData<List<OnlineGame>> getOnlineGames() {
        return onlineGames;
    }

    public void refreshOnlineGames() {
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("players").document(fAuth.getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
                List<String> gameIdentifiers = new ArrayList<>();

                Object gamesObj = documentSnapshot.get("games");
                if (gamesObj instanceof ArrayList<?>) {
                    ArrayList<?> gamesList = (ArrayList<?>) gamesObj;
                    for (Object gameObj : gamesList) {
                        if (gameObj instanceof String) {
                            gameIdentifiers.add((String) gameObj);
                        }
                    }
                }
                Log.d("GameIdentifiers", gameIdentifiers.toString());

                List<OnlineGame> games = new ArrayList<>();
                for (String gameIdentifier : gameIdentifiers) {
                    db.collection("games").document(gameIdentifier).get().addOnSuccessListener(documentSnapshot2 -> {
                        if (documentSnapshot2.exists()) {
                            games.add(OnlineGame.createFromDocument(documentSnapshot2));
                        }

                        // Notify if all games have been loaded
                        if (games.size() == gameIdentifiers.size()) {
                            onlineGames.setValue(games);
                        }
                    });
                }
            });
        }
    }
}
