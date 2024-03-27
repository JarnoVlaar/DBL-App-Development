package nl.tue.stratagrids;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;

import nl.tue.stratagrids.ui.game.GameBoardView;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private List<OnlineGame> localDataSet;

    Context context;

    FirebaseAuth fAuth;

    static final String TAG = "RecycleViewAdapter";

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textScorePlayer1;
        private final TextView textScorePlayer2;
        private final nl.tue.stratagrids.ui.game.GameBoardView gameBoardView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textViewTitle = view.findViewById(R.id.textGameAgainst);
            textScorePlayer1 =  view.findViewById(R.id.textScorePlayer1);
            textScorePlayer2 = view.findViewById(R.id.textScorePlayer2);
            gameBoardView = view.findViewById(R.id.gameBoardMatchPreview);


        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public TextView getTextScorePlayer1() {
            return textScorePlayer1;
        }

        public TextView gettextScorePlayer2() {
            return textScorePlayer2;
        }

        public nl.tue.stratagrids.ui.game.GameBoardView getgameBoardView() {
            return gameBoardView;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public RecycleViewAdapter(List<OnlineGame> dataSet, Context context) {
        this.localDataSet = dataSet;
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.match_overview, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // TODO: Make it change values based on online game data.

        Map<Integer, Integer> scores = localDataSet.get(position).getScores();
        viewHolder.getTextScorePlayer1().setText(context.getString(R.string.number,scores.get(1)));
        viewHolder.gettextScorePlayer2().setText(context.getString(R.string.number,scores.get(2)));
        viewHolder.getgameBoardView().updateGameWith(localDataSet.get(position));

        // Set title
        String opponentID = localDataSet.get(position).getOpponentID(fAuth.getCurrentUser().getUid());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User Collection")
                // Searching for all documents with field UserID matching current users ID.
                .whereEqualTo("UserID",opponentID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            viewHolder.getTextViewTitle().setText(context.getString(R.string.game_against, document.getString("Username")));


                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });



         ;

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.getTextView().setText(localDataSet[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}