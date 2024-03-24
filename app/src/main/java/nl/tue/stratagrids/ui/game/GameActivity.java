package nl.tue.stratagrids.ui.game;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import nl.tue.stratagrids.OfflineGame;
import nl.tue.stratagrids.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_offline_game);

        OfflineGame game = new ViewModelProvider(this).get(OfflineGame.class);
        TextView tvTurnIndicator = findViewById(R.id.tvTurn);
        TextView tvScoreP1 = findViewById(R.id.tvPlayer1Score);
        TextView tvScoreP2 = findViewById(R.id.tvPlayer2Score);

        // Set observers
        game.getCurrentPlayer().observe(this, currentPlayer -> {
            switch (currentPlayer) {
                case 1:
                    tvTurnIndicator.setText(R.string.local_p1_turn);
                    break;
                case 2:
                    tvTurnIndicator.setText(R.string.local_p2_turn);
                    break;
            }
        });
        game.getTurnCount().observe(this, turnCount -> {
            Map<Integer, Integer> scores = game.getScores();
            tvScoreP1.setText(String.valueOf(scores.getOrDefault(1, 0)));
            tvScoreP2.setText(String.valueOf(scores.getOrDefault(2, 0)));
        });

        // Back button functionality
        findViewById(R.id.BackButton).setOnClickListener(view -> finish());

        // Register game board view
        GameBoardView gameBoardView = findViewById(R.id.gameBoard);
        gameBoardView.updateGameWith(game);
    }
}