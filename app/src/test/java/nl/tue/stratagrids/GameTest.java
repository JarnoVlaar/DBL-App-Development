package nl.tue.stratagrids;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class GameTest {
    //The game instance that is used for testing
    private Game gameInstance;


    //game size for default tests
    final private int defaultSize = 4;

    //player count for default tests
    final private int defaultPlayerCount = 2;

    /**
     * Set up before each test
     *
     * @throws Exception no exceptions thrown yet
     */
    @Before
    public void setUp() throws Exception {
        gameInstance = new Game(defaultSize, defaultPlayerCount);
    }

    /**
     * Clean up after each test
     *
     * @throws Exception no exceptions thrown yet
     */
    @After
    public void tearDown() throws Exception {
    }

    //----------------------------------------------------------------------------
    //Tests for getters
    //----------------------------------------------------------------------------

    /**
     * Test the method Game.getSize()
     */
    @Test
    public void testGetSize() {
        System.out.println("Testing Game.getSize()");

        assertEquals("Game.getSize() does not return the correct value", defaultSize, gameInstance.getSize());
    }

    /**
     * Test the method Game.getVerticalLines()
     */
    @Test
    public void testGetVerticalLines() {
        System.out.println("Testing Game.getVerticalLines()");

        int[][] verticalLines = gameInstance.getVerticalLines();

        assertEquals("verticalLines sizeY is not correct", defaultSize, verticalLines.length);

        for (int i = 0; i < defaultSize; i++) {
            assertEquals("verticalLines sizeX is not correct", defaultSize - 1, verticalLines[i].length);
        }

        for (int i = 0; i < defaultSize; i++) {
            for (int j = 0; j < defaultSize - 1; j++) {
                assertEquals("Not the correct value", 0, verticalLines[i][j]);
            }
        }
    }

    /**
     * Test the method Game.getHorizontalLines()
     */
    @Test
    public void testGetHorizontalLines() {
        System.out.println("Testing Game.getHorizontalLines()");

        int[][] horizontalLines = gameInstance.getHorizontalLines();

        assertEquals("verticalLines sizeY is not correct", defaultSize - 1, horizontalLines.length);

        for (int i = 0; i < defaultSize - 1; i++) {
            assertEquals("verticalLines sizeX is not correct", defaultSize, horizontalLines[i].length);
        }

        for (int i = 0; i < defaultSize - 1; i++) {
            for (int j = 0; j < defaultSize; j++) {
                assertEquals("Not the correct value", 0, horizontalLines[i][j]);
            }
        }
    }

    /**
     * Test the method Game.getCapturedBlocks()
     */
    @Test
    public void testGetcapturedBlocks() {
        System.out.println("Testing Game.getCapturedBlocks()");

        int[][] capturedBlocks = gameInstance.getCapturedBlocks();

        assertEquals("verticalLines sizeY is not correct", defaultSize - 1, capturedBlocks.length);

        for (int i = 0; i < defaultSize - 1; i++) {
            assertEquals("verticalLines sizeX is not correct", defaultSize - 1, capturedBlocks[i].length);
        }

        for (int i = 0; i < defaultSize - 1; i++) {
            for (int j = 0; j < defaultSize - 1; j++) {
                assertEquals("Not the correct value", 0, capturedBlocks[i][j]);
            }
        }
    }

    /**
     * Test the method Game.getCurrentPlayer()
     */
    @Test
    public void testGetCurrentPlayer() {
        System.out.println("Testing Game.getCurrentPlayer()");

        assertEquals("First getCurrentPlayer test is wrong", 1, gameInstance.getCurrentPlayer());

        gameInstance.makeMove(1,2, 2);

        assertEquals("First getCurrentPlayer test is wrong", 2, gameInstance.getCurrentPlayer());
    }

    /**
     * Test Game.getScores()
     */
    @Test
    public void testGetScores() {
        int[][] horizontalLines = new int[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        int[][] verticalLines = new int[][]{
                {1, 0, 0},
                {1, 0, 0},
                {0, 0, 1},
                {0, 0, 0}
        };

        int[][] capturedBlocks = new int[][] {
                {1, 1, 2},
                {0, 1, 2},
                {2, 2, 2}
        };

        gameInstance = new Game(verticalLines, horizontalLines, capturedBlocks,
                defaultSize, 1, defaultPlayerCount );

        Map<Integer, Integer> scores = gameInstance.getScores();

        int score1 = scores.get(1);
        int score2 = scores.get(2);

        Assert.assertEquals("Player 1 score is not correct", 3, score1);
        Assert.assertEquals("Player 2 score is not correct", 5, score2);
    }

    //----------------------------------------------------------------------------
    //Tests for makeMove()
    //----------------------------------------------------------------------------

    /**
     * Default method to test if arrays are equal.
     *
     * @param expectedHorizontalLines Expected value of the first array
     * @param expectedVerticalLines Expected value of the second array
     * @param horizontalLines value of first array
     * @param verticalLines value of second array
     */
    private void assertLinesEqual(int[][] expectedHorizontalLines, int[][] expectedVerticalLines,
                                  int[][] horizontalLines, int[][] verticalLines) {

        assertEquals("Sizes are not equal", expectedHorizontalLines.length, horizontalLines.length);
        for (int i = 0; i < expectedHorizontalLines.length; i++) {
            assertEquals("Sizes are not equal", expectedHorizontalLines[i].length, horizontalLines[i].length);
            for (int j = 0; j < expectedHorizontalLines[i].length; j++) {
                assertEquals("Horizontal lines are not correct",
                        expectedHorizontalLines[i][j], horizontalLines[i][j]);
            }
        }

        assertEquals("Sizes are not equal", expectedVerticalLines.length, expectedVerticalLines.length);
        for (int i = 0; i < expectedVerticalLines.length; i++) {
            assertEquals("Sizes are not equal", expectedVerticalLines[i].length, expectedVerticalLines[i].length);
            for (int j = 0; j < expectedVerticalLines[i].length; j++) {
                assertEquals("Vertical lines are not correct",
                        expectedVerticalLines[i][j], verticalLines[i][j]);
            }
        }
    }

    /**
     * Test valid vertical move on Game.MakeMove()
     */
    @Test
    public void testValidVerticalMove() {
        System.out.println("Testing game.makeMove() vertical move");

        gameInstance.makeMove(2,2, 1);

        int[][] horizontalLines = gameInstance.getHorizontalLines();
        int[][] verticalLines = gameInstance.getVerticalLines();

        int[][] expectedHorizontalLines = new int[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        int[][] expectedVerticalLines = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 1},
                {0, 0, 0}
        };

        assertLinesEqual(expectedHorizontalLines, expectedVerticalLines, horizontalLines, verticalLines);
    }

    /**
     * Test valid horizontal move on Game.makeMove()
     */
    @Test
    public void testValidHorizontalMove() {
        System.out.println("Testing game.makeMove() horizontal move");

        gameInstance.makeMove(2,2, 1);

        int[][] horizontalLines = gameInstance.getHorizontalLines();
        int[][] verticalLines = gameInstance.getVerticalLines();

        int[][] expectedHorizontalLines = new int[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        int[][] expectedVerticalLines = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 1},
                {0, 0, 0}
        };

        assertLinesEqual(expectedHorizontalLines, expectedVerticalLines, horizontalLines, verticalLines);
    }

    /**
     * Test multiple moves on Game.makeMove();
     */
    @Test
    public void testValidMoves() {
        System.out.println("Testing game.makeMove() multiple moves");

        gameInstance.makeMove(0,2, 2);
        gameInstance.makeMove(1,2, 2);
        gameInstance.makeMove(2,3, 2);
        gameInstance.makeMove(0,0, 1);

        int[][] horizontalLines = gameInstance.getHorizontalLines();
        int[][] verticalLines = gameInstance.getVerticalLines();

        int[][] expectedHorizontalLines = new int[][]{
                {0, 0, 1, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 1}
        };

        int[][] expectedVerticalLines = new int[][]{
                {2, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };

        assertLinesEqual(expectedHorizontalLines, expectedVerticalLines, horizontalLines, verticalLines);
    }

    /**
     * Test duplicate move
     */
    @Test
    public void testDuplicateMove() {
        System.out.println("Testing game.makeMove() duplicate");

        Assert.assertTrue("Failed valid move", gameInstance.makeMove(0, 1, 2));
        Assert.assertFalse("Succesful invalid move", gameInstance.makeMove(0,1, 2));

        int[][] horizontalLines = gameInstance.getHorizontalLines();
        int[][] verticalLines = gameInstance.getVerticalLines();

        int[][] expectedHorizontalLines = new int[][]{
                {0, 1, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        int[][] expectedVerticalLines = new int[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };

        assertLinesEqual(expectedHorizontalLines, expectedVerticalLines, horizontalLines, verticalLines);
    }

    /**
     * Test invalid move preset, used in later tests
     */
    public void testInvalidMoves(int x, int y, String expectedMessage) {
        System.out.println("Testing game.makeMove() invalid move x: " + x + " - y: " + y);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> gameInstance.makeMove(x,y,1));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Test a x value that is negative
     */
    @Test
    public void testNegativeX() {
        testInvalidMoves(-1, 1, "inputted x cannot be negative");
    }

    /**
     * Test a y value that is negative
     */
    @Test
    public void testNegativeY() {
        testInvalidMoves(1, -1, "inputted y cannot be negative");
    }

    /**
     * Test a x value that is bigger than the board size
     */
    @Test
    public void testInvalidX() {
        testInvalidMoves(1000, 1, "inputted x cannot be bigger than the board size");
    }

    /**
     * Test a y value that is bigger than the board size
     */
    @Test
    public void testInvalidY() {
        testInvalidMoves(1, 1000, "inputted y cannot be bigger than the board size");
    }
}