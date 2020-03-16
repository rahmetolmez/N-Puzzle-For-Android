package com.tribow.npuzzle;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import java.util.Random;

public class LayoutActivityGameplay extends View
{
    private int[][] boardMap;
    private int sizeX = 0, sizeY = 0;
    private int blankX = 0, blankY = 0;
    private int numOfMoves = 0;
    private boolean close = false;
    private boolean shouldVibrate = true;
    private boolean optionDarkMode = false;
    private Bitmap one_bm;
    private Bitmap one_bm_r;
    private Bitmap yellow_block_bm;
    private Bitmap yellow_block_bm_r;
    private Bitmap black_block_bm;
    private Bitmap black_block_bm_r;
    private Bitmap windowPuzzleSolved;

    private Paint pb_stroke;
    private Paint paint_block;
    private Paint paint_moves;
    private Paint pb_gameEnd;
    private Rect block;

    private int paddingX = 70;
    private int paddingY;
    private int blockWidth = 0;

    private Vibrator vibrator;

    public LayoutActivityGameplay(Context context)
    {
        super(context);

        handleDarkMode();
        handlePaint(context);
        handleBitmap();

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        block = new Rect();
        paddingY = 3 * (int) getNavigationBarHeight();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(isSolved())
        {
            sceneGameEnd(canvas);
        }
        if(!close)
        {
            sceneGameplay(canvas);

            invalidate();
        }
    }

    public void createBoard(int X, int Y)
    {
        boardMap = new int[Y][X];
    }

    public void setBoardArray(int[][] boardMap)
    {
        for(int i = 0; i < sizeY; i++)
            for(int j = 0; j < sizeX; j++)
                this.boardMap[i][j] = boardMap[i][j];
    }
    public void setXY(int sizeX, int sizeY)
    {
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        createBoard(sizeX, sizeY);
    }

    public String toString()
    {
        String boardStr = "";

        for (int i = 0; i < sizeY; i++)
        {
            for (int j = 0; j < sizeX; j++)
            {
                if (boardMap[i][j] == 0)
                {
                    boardStr += String.format("\t");
                }
                else if (boardMap[i][j] == -1)
                {
                    boardStr += String.format("\t");
                    boardStr += String.format("00");
                }
                else
                {
                    boardStr += String.format("\t");
                    boardStr += String.format("%d", boardMap[i][j]);
                }
            }
            boardStr += String.format("\n");
        }
        return boardStr;
    }

    public void moveManager(char move)
    {
        move(move);
        if(shouldVibrate)
            vibrator.vibrate(30);
        numOfMoves++;
    }

    public char move(char move)
    {
        findBlankPosition();

        if (move == 'q' || move == 'Q')
        {
            System.out.println("\nThanks for playing! Bye...");
            close = true;
        }
        else if (move == 'l' || move == 'L')
        {
            if (blankX != 0 && boardMap[blankY][blankX - 1] != -1)
            {
                boardMap[blankY][blankX] = boardMap[blankY][blankX - 1];
                boardMap[blankY][blankX - 1] = 0;
                return 'L';
            }
        }
        else if (move == 'r' || move == 'R')
        {
            if (blankX != sizeX - 1 && boardMap[blankY][blankX + 1] != -1)
            {
                boardMap[blankY][blankX] = boardMap[blankY][blankX + 1];
                boardMap[blankY][blankX + 1] = 0;
                return 'R';
            }
        }
        else if (move == 'u' || move == 'U')
        {
            if (blankY != 0 && boardMap[blankY - 1][blankX] != -1)
            {
                boardMap[blankY][blankX] = boardMap[blankY - 1][blankX];
                boardMap[blankY - 1][blankX] = 0;
                return 'U';
            }
        }
        else if (move == 'd' || move == 'D')
        {
            if (blankY != sizeY - 1 && boardMap[blankY + 1][blankX] != -1)
            {
                boardMap[blankY][blankX] = boardMap[blankY + 1][blankX];
                boardMap[blankY + 1][blankX] = 0;
                return 'D';
            }
        }
        return 'G';
    }

    public void findBlankPosition()
    {
        for (int i = 0; i < (sizeY); i++)
        {
            for (int j = 0; j < (sizeX); j++)
            {
                if (boardMap[i][j] == 0)
                {
                    blankY = i;
                    blankX = j;
                }
            }
        }
    }

    public boolean isSolved()
    {
        int count = 1;
        for (int i = 0; i < sizeY; i++)
        {
            for (int j = 0; j < sizeX; j++)
            {
                if (count != sizeX*sizeY)
                {
                    if (boardMap[i][j] != count)
                        return false;
                }
                count++;
            }
        }
        return true;
    }

    public void shuffleBoard(int shuffleMagnitude)
    {
        for(int i = 0; i < shuffleMagnitude * shuffleMagnitude; i++)
        {
            Random rand = new Random();
            int randomNum = rand.nextInt(4);

            if(randomNum == 0)
                move('R');
            else if(randomNum == 1)
                move('L');
            else if(randomNum == 2)
                move('U');
            else if(randomNum == 3)
                move('D');
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
            findBlankPosition();

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(event.getX() >= paddingX && event.getX() <= paddingX + sizeX * blockWidth &&
                        event.getY() >= paddingY && event.getY() <= paddingY + sizeY * blockWidth)
                    {
                        if (event.getX() >= paddingX + (blankX - 1) * blockWidth && event.getX() <= paddingX + ((blankX - 1) + 1) * blockWidth &&
                                event.getY() >= paddingY + blankY * blockWidth && event.getY() <= paddingY + (blankY + 1) * blockWidth)
                            moveManager('L');
                        else if (event.getX() >= paddingX + (blankX + 1) * blockWidth && event.getX() <= paddingX + ((blankX + 1) + 1) * blockWidth &&
                                event.getY() >= paddingY + blankY * blockWidth && event.getY() <= paddingY + (blankY + 1) * blockWidth)
                            moveManager('R');
                        else if (event.getX() >= paddingX + blankX * blockWidth && event.getX() <= paddingX + (blankX + 1) * blockWidth &&
                                event.getY() >= paddingY + (blankY - 1) * blockWidth && event.getY() <= paddingY + (blankY) * blockWidth)
                            moveManager('U');
                        else if (event.getX() >= paddingX + blankX * blockWidth && event.getX() <= paddingX + (blankX + 1) * blockWidth &&
                                event.getY() >= paddingY + (blankY + 1) * blockWidth && event.getY() <= paddingY + (blankY + 2) * blockWidth)
                            moveManager('D');
                    }
            }

        return super.onTouchEvent(event);
    }

    private void sceneGameplay(Canvas canvas)
    {
        int cHeight = getHeight(); //used to be canvas.getHeight, changed
        int cWidth = getWidth();

        paint_block.setTextAlign(Paint.Align.CENTER);
        paint_block.setColor(Color.WHITE);
        paint_block.setAlpha(200);

        int checkX = (getWidth() - paddingX * 2) / sizeX;
        int checkY = (getHeight() - paddingY * 2) / sizeY;

        if(checkX <= checkY)
        {
            blockWidth = (getWidth() - paddingX * 2) / sizeX;
            paddingY = (getHeight() - sizeY * blockWidth) / 2;
        }
        else
        {
            blockWidth = (getHeight() - paddingY * 2) / sizeY;
            paddingX = (getWidth() - sizeX * blockWidth) / 2;
        }

        one_bm_r = Bitmap.createScaledBitmap(one_bm, blockWidth-15, blockWidth-15, true);
        black_block_bm_r = Bitmap.createScaledBitmap(black_block_bm, blockWidth, blockWidth, true);

        String numStr;
        String movesMadeStr = "Moves made: " + numOfMoves;
        Rect rect_textBound = new Rect();
        int toLeft = 20;
        int toTop = 20;

        for (int i = 0; i < sizeY; i++)
        {
            for (int j = 0; j < sizeX; j++)
            {
                block.set(paddingX + j * blockWidth, paddingY + i * blockWidth, paddingX + (j + 1) * blockWidth, paddingY + (i + 1) * blockWidth);
                //canvas.drawRect(block, pb_stroke);

                if(boardMap[i][j] == 0)
                {
                    numStr = "";
                    //canvas.drawBitmap(black_block_bm_r,paddingX + j * blockWidth,paddingY + i * blockWidth,null);
                }
                else
                {
                    numStr = "" + boardMap[i][j];
                    canvas.drawBitmap(one_bm_r,paddingX + j * blockWidth,paddingY + i * blockWidth,null);
                }

                paint_block.getTextBounds(numStr, 0, numStr.length(), rect_textBound);
                paint_block.setTextSize(blockWidth / 2);

                canvas.drawText(numStr, paddingX + j * blockWidth + blockWidth / 2, paddingY + (i+1) * blockWidth - toTop, paint_block);
            }
        }
        paint_moves.setTextSize(getWidth() / 18);
        Rect bounds = new Rect();
        paint_moves.getTextBounds(movesMadeStr, 0, movesMadeStr.length(), bounds);
        canvas.drawText(movesMadeStr, cWidth / 2, cHeight - 150, paint_moves);
    }

    private void sceneGameEnd(Canvas canvas)
    {
        paint_moves.setColor(Color.WHITE);

        String puzzleSolvedStr = "Puzzle solved!";
        String tapToContinueStr = "tap anywhere to continue";
        pb_gameEnd.setTextAlign(Paint.Align.CENTER);
        pb_gameEnd.setTextSize(getWidth() / 9);
        pb_gameEnd.setColor(Color.WHITE);
        canvas.drawText(puzzleSolvedStr, getWidth() / 2, getHeight() / 3, pb_gameEnd);
        paint_moves.setTextAlign(Paint.Align.CENTER);

        //converting 42dp (height of navigation bar) to pixels
        float dip = 42f;
        Resources r = getResources();

        float dpToPixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        // Get the bounds of the text, using our testTextSize.
        paint_moves.setTextSize(getWidth() / 18);
        Rect bounds = new Rect();
        paint_moves.getTextBounds(tapToContinueStr, 0, tapToContinueStr.length(), bounds);

        canvas.drawText(tapToContinueStr, getWidth()/2, getHeight() - 2 * dpToPixel, paint_moves);

        canvas.drawText("moves made: " + numOfMoves, getWidth() / 2, getHeight() / 2, paint_moves);
        close = true;
    }

    public boolean getClose()
    {
        return close;
    }

    public void drawButton(Canvas canvas, int left, int top, int right, int bottom)
    {
        paint_moves.setColor(Color.RED);
        canvas.drawRect(left, top, right, bottom, paint_moves);
    }
    public float getNavigationBarHeight()
    {
        float dip = 42f;
        Resources r = getResources();

        float dpToPixel = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );
        return dpToPixel;
    }

    public void setOptionVibrate(boolean shouldVibrate)
    {
        this.shouldVibrate = shouldVibrate;
    }

    public void setDarkMode(boolean optionDarkMode)
    {
        this.optionDarkMode = optionDarkMode;
        setBackgroundColor(Color.BLACK);
    }

    private void handleDarkMode()
    {
        if(optionDarkMode)
            setBackgroundColor(Color.BLACK);
        else
            setBackgroundResource(R.drawable.bg_bricks_blue_blur);
    }

    private void handlePaint(Context context)
    {
        paint_block = new Paint();
        paint_moves = new Paint();
        pb_gameEnd = new Paint();

        pb_stroke = new Paint();
        pb_stroke.setColor(Color.BLACK);
        pb_stroke.setStrokeWidth(10);
        pb_stroke.setStyle(Paint.Style.STROKE);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.de);
        paint_block.setTypeface(typeface);

        Typeface typefaceGameEnd = ResourcesCompat.getFont(context, R.font.gals);
        pb_gameEnd.setTypeface(typefaceGameEnd);
        pb_gameEnd.setColor(Color.BLACK);
        pb_gameEnd.setTextSize(150);

        paint_moves.setTypeface(typefaceGameEnd);
        paint_moves.setTextAlign(Paint.Align.CENTER);
        paint_moves.setColor(Color.WHITE);
        paint_moves.setTextSize(60);
    }

    private void handleBitmap()
    {
        one_bm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_card_transblur_dark);
        black_block_bm = BitmapFactory.decodeResource(getResources(), R.drawable.background_black);
    }
}