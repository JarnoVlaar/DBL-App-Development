package nl.tue.stratagrids;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

/**
 * Game class that will be used to play the game, handles the game state and the game logic
 */
public class BaseGame {

    private final int[][] verticalLines;
    private final int[][] horizontalLines;
    private final int[][] capturedBlocks;

    private final int size;
    private final int players;

    protected final MutableLiveData<Integer> currentPlayer;
    protected final MutableLiveData<Integer> turnCount;


    /**
     * Game constructor for an existing game
     *
     * @param verticalLines   representing the vertical lines as a 2D array
     * @param horizontalLines representing the horizontal lines as a 2D array
     * @param capturedBlocks  representing the captured blocks as a 2D array
     * @param size            of the board measured in dots
     * @param currentPlayer   representing the index of the current player
     * @param players         representing the number of players in the game
     * @param turnCount       representing the number of turns that have been played
     */
    public BaseGame(int[][] verticalLines, int[][] horizontalLines, int[][] capturedBlocks, int size, int currentPlayer, int players, int turnCount) {
        this.verticalLines = verticalLines;
        this.horizontalLines = horizontalLines;
        this.capturedBlocks = capturedBlocks;
        this.size = size;
        this.players = players;
        this.currentPlayer = new MutableLiveData<>(currentPlayer);
        this.turnCount = new MutableLiveData<>(turnCount);
    }

    /**
     * Game constructor for a new game
     *
     * @param size    of the board measured in dots
     * @param players representing the number of players in the game
     */
    public BaseGame(int size, int players) {
        this.verticalLines = new int[size][size - 1];
        this.horizontalLines = new int[size - 1][size];
        this.capturedBlocks = new int[size - 1][size - 1];
        this.size = size;
        this.players = players;
        this.currentPlayer = new MutableLiveData<>(1);
        this.turnCount = new MutableLiveData<>(0);
    }

    /**
     * Getter for the vertical lines of the board
     *
     * @return the vertical lines of the board
     */
    public int[][] getVerticalLines() {
        return verticalLines;
    }

    /**
     * Getter for the horizontal lines of the board
     *
     * @return the horizontal lines of the board
     */
    public int[][] getHorizontalLines() {
        return horizontalLines;
    }

    /**
     * Getter for the captured blocks of the board
     *
     * @return the captured blocks of the board
     */
    public int[][] getCapturedBlocks() {
        return capturedBlocks;
    }

    /**
     * Getter for the size of the board
     *
     * @return the size of the board
     */
    public int getSize() {
        return size;
    }


    /**
     * Logic for making a move in the game, checks if a block is captured and updates the scores, checks if the current player gets another turn
     *
     * @param x         coordinate of the line
     * @param y         coordinate of the line
     * @param alignment of the line where 1 is vertical, 2 is horizontal
     * @return true if the move was successful
     * @modifies the verticalLines array
     * @modifies the horizontalLines array
     * @modifies the capturedBlocks array
     * @modifies the currentPlayer
     * @modifies the scores
     */
    public boolean makeMove(int x, int y, int alignment) {
        // Save the scores before the move
        HashMap<Integer, Integer> oldScores = checkScore();

        // alignment where 1 is vertical, 2 is horizontal
        if (alignment == 1) {
            if (verticalLines[x][y] != 0) {
                return false;
            }
            verticalLines[x][y] = currentPlayer.getValue();
        } else if (alignment == 2) {
            if (horizontalLines[x][y] != 0) {
                return false;
            }
            horizontalLines[x][y] = currentPlayer.getValue();
        }

        // Check all the blocks around the line
        if (alignment == 1) {
            if (x > 0) {
                checkFilled(x - 1, y);
            }
            if (x < size - 1) {
                checkFilled(x, y);
            }
        } else if (alignment == 2) {
            if (y > 0) {
                checkFilled(x, y - 1);
            }
            if (y < size - 1) {
                checkFilled(x, y);
            }
        }

        // get the new scores
        HashMap<Integer, Integer> newScores = checkScore();

        // check if the score of the current player has increased
        if (newScores.getOrDefault(currentPlayer, 0) > oldScores.getOrDefault(currentPlayer, 0)) {
            // if the score has increased, the player gets another turn
        } else {
            // if the score has not increased, the next player gets a turn
            switchPlayer();
        }

        turnCount.setValue(turnCount.getValue() + 1);
        return true;
    }


    /**
     * Function to check if a block is captured, also checks neighbouring blocks
     *
     * @param x coordinate of the block
     * @param y coordinate of the block
     * @modifies the capturedBlocks array
     */
    private void checkFilled(int x, int y) {
        if (x < 0 || x >= size - 1 || y < 0 || y >= size - 1) {
            // out of bounds
            return;
        }

        // check if a block is captured
        if (verticalLines[x][y] != 0 && verticalLines[x + 1][y] != 0 && horizontalLines[x][y] != 0 && horizontalLines[x][y + 1] != 0 && capturedBlocks[x][y] == 0) {
            capturedBlocks[x][y] = currentPlayer.getValue();
        }
    }

    /**
     * Function to check the scores of the players
     *
     * @return a HashMap with the scores of the players
     */
    private HashMap<Integer, Integer> checkScore() {
        HashMap<Integer, Integer> checkedScores = new HashMap<>();

        //initialize scores at 0 for each player
        for (int i = 1; i <= players; i++) {
            checkedScores.put(i, 0);
        }

        // check captured blocks
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) {
                if (capturedBlocks[i][j] != 0) {
                    checkedScores.put(capturedBlocks[i][j], checkedScores.getOrDefault(capturedBlocks[i][j], 0) + 1);
                }
            }
        }

        return checkedScores;
    }

    /**
     * Function to switch the current player to the next player
     *
     * @modifies currentPlayer to be the next player
     */
    private void switchPlayer() {
        assert currentPlayer != null;
        assert currentPlayer.getValue() != null;
        currentPlayer.setValue(currentPlayer.getValue() % players + 1);
    }

    /**
     * Getter for the current player of the game
     *
     * @return the current player
     */
    public LiveData<Integer> getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Getter for the turn count of the game
     *
     * @return the turn count
     */
    public LiveData<Integer> getTurnCount() {
        return turnCount;
    }

    /**
     * Function to get the scores of the players
     *
     * @return a HashMap with the scores of the players
     */
    public Map<Integer, Integer> getScores() {
        return checkScore();
    }
}