package nl.tue.stratagrids;

import java.util.HashMap;
import java.util.Map;

/**
 * Game class that will be used to play the game, handles the game state and the game logic
 */
public class Game {

    private final int[][] verticalLines;
    private final int[][] horizontalLines;
    private final int[][] capturedBlocks;

    private final int size;
    private final int players;
    private int currentPlayer;


    /**
     * Game constructor for an existing game
     * @param verticalLines representing the vertical lines as a 2D array
     * @param horizontalLines representing the horizontal lines as a 2D array
     * @param capturedBlocks representing the captured blocks as a 2D array
     * @param size of the board measured in dots
     * @param currentPlayer representing the current player
     * @param players representing the number of players in the game
     */
    public Game(int[][] verticalLines, int[][] horizontalLines, int[][] capturedBlocks, int size, int currentPlayer, int players){
        this.verticalLines = verticalLines;
        this.horizontalLines = horizontalLines;
        this.capturedBlocks = capturedBlocks;
        this.size = size;
        this.currentPlayer = currentPlayer;
        this.players = players;
    }

    /**
     * Game constructor for a new game
     * @param size of the board measured in dots
     * @param players representing the number of players in the game
     */
    public Game(int size, int players){
        this.verticalLines = new int[size][size-1];
        this.horizontalLines = new int[size-1][size];
        this.capturedBlocks = new int[size-1][size-1];
        this.size = size;
        this.players = players;
        this.currentPlayer = 1;
    }

    /**
     * Getter for the vertical lines of the board
     * @return the vertical lines of the board
     */
    public int[][] getVerticalLines() {
        return verticalLines;
    }

    /**
     * Getter for the horizontal lines of the board
     * @return the horizontal lines of the board
     */
    public int[][] getHorizontalLines() {
        return horizontalLines;
    }

    /**
     * Getter for the captured blocks of the board
     * @return the captured blocks of the board
     */
    public int[][] getCapturedBlocks() {
        return capturedBlocks;
    }

    /**
     * Getter for the size of the board
     * @return the size of the board
     */
    public int getSize() {
        return size;
    }


    /**
     * Logic for making a move in the game, checks if a block is captured and updates the scores, checks if the current player gets another turn
     * @param x coordinate of the line
     * @param y coordinate of the line
     * @param alignment of the line where 1 is vertical, 2 is horizontal
     * @modifies the verticalLines array
     * @modifies the horizontalLines array
     * @modifies the capturedBlocks array
     * @modifies the currentPlayer
     * @modifies the scores
     * @return true if the move was successful
     *
     * @throws IllegalArgumentException if {@code x < 0 || x >= this.size || y < 0 || y >= this.size }
     */
    public boolean makeMove(int x, int y, int alignment) throws IllegalArgumentException {
        if (x < 0) {
            throw new IllegalArgumentException("inputted x cannot be negative");
        }
        if (x >= this.size) {
            throw new IllegalArgumentException("inputted x cannot be bigger than the board size");
        }
        if (y < 0) {
            throw new IllegalArgumentException("inputted y cannot be negative");
        }
        if (y >= this.size) {
            throw new IllegalArgumentException("inputted y cannot be bigger than the board size");
        }

        // Save the scores before the move
        HashMap<Integer,Integer> oldScores = checkScore();

        // alignment where 1 is vertical, 2 is horizontal
        if (alignment == 1) {
            if (verticalLines[x][y] != 0) {
                return false;
            }
            verticalLines[x][y] = currentPlayer;
        } else if (alignment == 2) {
            if (horizontalLines[x][y] != 0) {
                return false;
            }
            horizontalLines[x][y] = currentPlayer;
        }

        // Check all the blocks around the line
        if (alignment == 1) {
            if (x > 0) {
                checkFilled(x-1, y);
            }
            if (x < size-1) {
                checkFilled(x, y);
            }
        } else if (alignment == 2) {
            if (y > 0) {
                checkFilled(x, y-1);
            }
            if (y < size-1) {
                checkFilled(x, y);
            }
        }

        // get the new scores
        HashMap<Integer,Integer> newScores = checkScore();

        // check if the score of the current player has increased
        if (newScores.getOrDefault(currentPlayer, 0) > oldScores.getOrDefault(currentPlayer, 0)) {
            // if the score has increased, the player gets another turn
        } else {
            // if the score has not increased, the next player gets a turn
            switchPlayer();
        }

        return true;
    }


    /**
     * Function to check if a block is captured, also checks neighbouring blocks
     * @param x coordinate of the block
     * @param y coordinate of the block
     * @modifies the capturedBlocks array
     */
    private void checkFilled(int x, int y) {
        if (x < 0 || x >= size-1 || y < 0 || y >= size-1) {
            // out of bounds
            return;
        }

        // check if a block is captured
        if (verticalLines[x][y] != 0 && verticalLines[x+1][y] != 0 && horizontalLines[x][y] != 0 && horizontalLines[x][y+1] != 0 && capturedBlocks[x][y] == 0) {
            capturedBlocks[x][y] = currentPlayer;
        }
    }

    /**
     * Function to check the scores of the players
     * @return a HashMap with the scores of the players
     */
    private HashMap<Integer, Integer> checkScore() {
        HashMap<Integer, Integer> checkedScores = new HashMap<>();

        //initialize scores at 0 for each player
        for (int i = 1; i <= players; i++) {
            checkedScores.put(i, 0);
        }

        // check captured blocks
        for (int i = 0; i < size-1; i++) {
            for (int j = 0; j < size-1; j++) {
                if (capturedBlocks[i][j] != 0) {
                    checkedScores.put(capturedBlocks[i][j], checkedScores.getOrDefault(capturedBlocks[i][j], 0) + 1);
                }
            }
        }

        return checkedScores;
    }

    /**
     * Function to switch the current player to the next player
     * @modifies currentPlayer to be the next player
     */
    private void switchPlayer() {
        if (currentPlayer == players) {
            currentPlayer = 1;
        } else {
            currentPlayer++;
        }
    }

    /**
     * Getter for the current player of the game
     * @return the current player
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Function to get the scores of the players
     * @return a HashMap with the scores of the players
     */
    public Map<Integer,Integer> getScores() {
        return checkScore();
    }
}