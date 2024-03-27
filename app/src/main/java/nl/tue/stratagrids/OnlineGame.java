package nl.tue.stratagrids;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class OnlineGame extends BaseGame {

    private final String gameId;
    private final String winnerId;
    private final String[] playerIds;

    public OnlineGame(int[][] capturedBlocks, int[][] verticalLines, int[][] horizontalLines, int size, String[] playerIds, int playerTurn, int turnCount, String gameId, String winnerId) {
        super(verticalLines, horizontalLines, capturedBlocks, size, playerTurn, playerIds.length, turnCount);
        this.gameId = gameId;
        this.winnerId = winnerId;
        this.playerIds = playerIds;
    }

    public static OnlineGame createFromDocument(DocumentSnapshot document) {
        if (document == null) {
            return null;
        }
        if (!document.contains("capturedBlocks") || !document.contains("verticalLines") || !document.contains("horizontalLines") ||
                !document.contains("gridSize") || !document.contains("playerIDs") || !document.contains("playerTurn") || !document.contains("turnCount") ||
                !document.contains("winnerID")) {
            return null;
        }
        String winnerId = document.getString("winnerID");
        int size = document.getLong("gridSize").intValue();
        int playerTurn = document.getLong("playerTurn").intValue();
        int turnCount = document.getLong("turnCount").intValue();
        ArrayList<String> playerIds = (ArrayList<String>) document.get("playerIDs");
        String[] playerIdsArray = new String[playerIds.size()];
        playerIdsArray = playerIds.toArray(playerIdsArray);
        int[][] capturedBlocks = new int[size - 1][size - 1];
        int[][] verticalLines = new int[size][size - 1];
        int[][] horizontalLines = new int[size - 1][size];
        ArrayList<Long> capturedBlocksList = (ArrayList<Long>) document.get("capturedBlocks");
        ArrayList<Long> verticalLinesList = (ArrayList<Long>) document.get("verticalLines");
        ArrayList<Long> horizontalLinesList = (ArrayList<Long>) document.get("horizontalLines");
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                capturedBlocks[i][j] = capturedBlocksList.get(i * (size - 1) + j).intValue();
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                verticalLines[i][j] = verticalLinesList.get(i * (size - 1) + j).intValue();
            }
        }
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size; j++) {
                horizontalLines[i][j] = horizontalLinesList.get(i * size + j).intValue();
            }
        }
        return new OnlineGame(capturedBlocks, verticalLines, horizontalLines, size, playerIdsArray, playerTurn, turnCount, document.getId(), winnerId);
    }

    //TODO: Add methods for; Retrieving opponent name, Getting if it your turn or not
}
