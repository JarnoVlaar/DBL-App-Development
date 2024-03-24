package nl.tue.stratagrids.ui.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import nl.tue.stratagrids.AbstractGame;

public class GameBoardView extends View {
    private static final int DOT_RADIUS = 12;
    private static final int DOT_SPACING = 48;
    private static final int DOT_STROKE_WIDTH = 3;
    private static final int LINE_WIDTH = 12;
    private static final int LINE_STROKE_WIDTH = 3;

    private static final int PLAYER1_COLOR = 0xFF3AC0A0;
    private static final int PLAYER2_COLOR = 0xFFFFB37C;
    private static final int NO_PLAYER_COLOR = 0xFFEAF2FF;
    private static final int STROKE_COLOR = 0xFF494A50;

    private final Paint[] playerPaints;
    private final Paint strokePaint;

    private float xDown;
    private float yDown;

    @Nullable
    private AbstractGame game;

    public void updateGameWith(AbstractGame game) {
        this.game = game;
        invalidate();
    }

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        playerPaints = new Paint[3];
        for (int i = 0; i < 3; i++) {
            playerPaints[i] = new Paint();
            playerPaints[i].setStyle(Paint.Style.FILL);
            playerPaints[i].setAntiAlias(true);
        }
        playerPaints[0].setColor(NO_PLAYER_COLOR);
        playerPaints[1].setColor(PLAYER1_COLOR);
        playerPaints[2].setColor(PLAYER2_COLOR);

        strokePaint = new Paint();
        strokePaint.setColor(STROKE_COLOR);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
    }

    public int getUnscaledWidth() {
        return (game.getSize() - 1) * DOT_SPACING + DOT_RADIUS * 2;
    }

    public float getScaleFactor() {
        return (float) getWidth() / getUnscaledWidth();
    }

    public int getStartY() {
        return (int) ((getHeight() - getUnscaledWidth() * getScaleFactor()) / 2);
    }

    public int getXFromColumn(int column) {
        return (int) (column * DOT_SPACING * getScaleFactor() + DOT_RADIUS * getScaleFactor());
    }

    public int getYFromRow(int row) {
        return (int) (row * DOT_SPACING * getScaleFactor() + DOT_RADIUS * getScaleFactor() + getStartY());
    }

    public int getColumnFromX(float x) {
        return (int) ((x - DOT_RADIUS * getScaleFactor() + DOT_SPACING / 2.0f * getScaleFactor()) / (DOT_SPACING * getScaleFactor()));
    }

    public int getRowFromY(float y) {
        return (int) ((y - DOT_RADIUS * getScaleFactor() - getStartY() + DOT_SPACING / 2.0f * getScaleFactor()) / (DOT_SPACING * getScaleFactor()));
    }

    public Point getInsideDot(int pointX, int pointY) {
        int column = getColumnFromX(pointX);
        int row = getRowFromY(pointY);
        double centerX = getXFromColumn(column);
        double centerY = getYFromRow(row);
        if (Math.sqrt(Math.pow(pointX - centerX, 2) + Math.pow(pointY - centerY , 2)) < DOT_RADIUS * 2.0f * getScaleFactor()) {
            return new Point(column, row);
        }
        return null;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (game == null) {
            return;
        }

        strokePaint.setStrokeWidth(DOT_STROKE_WIDTH * getScaleFactor());

        // Paint the captured blocks
        for (int i = 0; i < game.getSize() - 1; i++) {
            for (int j = 0; j < game.getSize() - 1; j++) {
                int x = getXFromColumn(i);
                int y = getYFromRow(j);
                int player = game.getCapturedBlocks()[i][j];
                if (player != 0) {
                    playerPaints[player].setStyle(Paint.Style.FILL);
                    canvas.drawRect(x, y, x + DOT_SPACING * getScaleFactor(), y + DOT_SPACING * getScaleFactor(), playerPaints[player]);
                }
            }
        }

        // Paint the dots
        for (int i = 0; i < game.getSize(); i++) {
            for (int j = 0; j < game.getSize(); j++) {
                int x = getXFromColumn(i);
                int y = getYFromRow(j);
                canvas.drawCircle(x, y, DOT_RADIUS * getScaleFactor(), playerPaints[0]);
                canvas.drawCircle(x, y, (DOT_RADIUS - DOT_STROKE_WIDTH / 2.0f) * getScaleFactor(), strokePaint);
            }
        }

        // Paint the lines
        for (int i = 0; i < game.getSize(); i++) {
            for (int j = 0; j < game.getSize(); j++) {
                if (i < game.getSize() - 1) {
                    int horizontalLine = game.getHorizontalLines()[i][j];
                    if (horizontalLine != 0) {
                        int x = getXFromColumn(i);
                        int y = getYFromRow(j);
                        playerPaints[horizontalLine].setStrokeWidth(LINE_WIDTH * getScaleFactor());
                        canvas.drawLine(x, y, x + DOT_SPACING * getScaleFactor(), y, playerPaints[horizontalLine]);

                        // Draw the stroke along both sides of the line
                        strokePaint.setStrokeWidth(LINE_STROKE_WIDTH * getScaleFactor());
                        float offset = LINE_WIDTH * getScaleFactor() / 2 - LINE_STROKE_WIDTH * getScaleFactor() / 2;
                        canvas.drawLine(x, y - offset, x + DOT_SPACING * getScaleFactor(), y - offset, strokePaint);
                        canvas.drawLine(x, y + offset, x + DOT_SPACING * getScaleFactor(), y + offset, strokePaint);
                    }
                }
                if (j < game.getSize() - 1) {
                    int verticalLine = game.getVerticalLines()[i][j];
                    if (verticalLine != 0) {
                        int x = getXFromColumn(i);
                        int y = getYFromRow(j);
                        playerPaints[verticalLine].setStrokeWidth(LINE_WIDTH * getScaleFactor());
                        canvas.drawLine(x, y, x, y + DOT_SPACING * getScaleFactor(), playerPaints[verticalLine]);

                        // Draw the stroke along both sides of the line
                        strokePaint.setStrokeWidth(LINE_STROKE_WIDTH * getScaleFactor());
                        float offset = LINE_WIDTH * getScaleFactor() / 2 - LINE_STROKE_WIDTH * getScaleFactor() / 2;
                        canvas.drawLine(x - offset, y, x - offset, y + DOT_SPACING * getScaleFactor(), strokePaint);
                        canvas.drawLine(x + offset, y, x + offset, y + DOT_SPACING * getScaleFactor(), strokePaint);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game == null) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = x;
                yDown = y;
                break;
            case MotionEvent.ACTION_UP:
                // Check if a connection is made between two dots
                Point startDot = getInsideDot((int) xDown, (int) yDown);
                Point endDot = getInsideDot((int) x, (int) y);

                if (startDot != null && endDot != null) {
                    if (startDot.x == endDot.x && Math.abs(startDot.y - endDot.y) == 1) {
                        // Horizontal line
                        if (game.makeMove(Math.min(startDot.x, endDot.x), Math.min(startDot.y, endDot.y), 1)) {
                            invalidate();
                        }
                    } else if (startDot.y == endDot.y && Math.abs(startDot.x - endDot.x) == 1) {
                        // Vertical line
                        if (game.makeMove(Math.min(startDot.x, endDot.x), Math.min(startDot.y, endDot.y), 2)) {
                            invalidate();
                        }
                    }
                }
                break;
        }

        return true;
    }

}
