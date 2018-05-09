package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by yusukeyohishida on 5/6/18.
 */

public class Scoreboard extends AdjustableDrawable {

    private static final String TAG = Scoreboard.class.getSimpleName();

    private int margin;
    private int pointsize;
    private int setsize;
    private int leftPointCount = 0;
    private int leftSetCount = 0;
    private int rightPointCount = 0;
    private int rightSetCount = 0;
    //private int textSize = 20;

    private RectangleArea leftPointBoard;
    private RectangleArea leftSetBoard;
    private RectangleArea rightPointBoard;
    private RectangleArea rightSetBoard;



    private int color = Color.WHITE;
    private int numberColor = Color.BLACK;

    public Scoreboard() {

        leftPointBoard = new RectangleArea(0,0,0,0, color);
        leftSetBoard = new RectangleArea(0,0,0,0, color);
        rightPointBoard = new RectangleArea(0,0,0,0, color);
        rightSetBoard = new RectangleArea(0, 0, 0, 0, color);

        leftPointBoard.setText("0");
        leftSetBoard.setText("0");
        rightPointBoard.setText("0");
        rightSetBoard.setText("0");

    }



    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        Log.d(TAG, "Drawing scoreboard");

        leftSetBoard.draw(canvas);
        leftPointBoard.draw(canvas);
        rightSetBoard.draw(canvas);
        rightPointBoard.draw(canvas);

        // draw numbers

        /*
        Paint paint = new Paint();
        paint.setColor(numberColor);
        paint.setTextSize(getTextSize());
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText(String.valueOf(leftSetCount), leftSetBoard.getCenterX(), leftSetBoard.getBottomY(), paint);
        canvas.drawText(String.valueOf(leftPointCount), leftPointBoard.getCenterX(), leftPointBoard.getBottomY(), paint);
        canvas.drawText(String.valueOf(rightSetCount), rightSetBoard.getCenterX(), rightSetBoard.getBottomY(), paint);
        canvas.drawText(String.valueOf(rightPointCount), rightPointBoard.getCenterX(), rightPointBoard.getBottomY(), paint);
        */

    }

    @Override
    public void adjustLayout(int width, int height) {
        Log.d(TAG, "Adjusting layout");


        // Adjust scoreboard
        margin = (int) (0.01 * width);
        pointsize = (int) (0.08 * height);
        setsize = (int) (0.06 * height);
        //textSize = (int) (0.9 * setsize);
        setSetTextSize((int)(0.8*setsize));
        setPointTextSize((int)(0.8*pointsize));


        int centerX = (int) (0.5 * width);

        leftPointBoard.setX(centerX-pointsize-setsize-2*margin);
        leftPointBoard.setY(2*margin);
        leftPointBoard.setHeight(pointsize);
        leftPointBoard.setWidth(pointsize);
        leftSetBoard.setX(centerX-setsize-margin);
        leftSetBoard.setY(2*margin+pointsize-setsize);
        leftSetBoard.setHeight(setsize);
        leftSetBoard.setWidth(setsize);
        rightPointBoard.setX(centerX+setsize+2*margin);
        rightPointBoard.setY(2*margin);
        rightPointBoard.setHeight(pointsize);
        rightPointBoard.setWidth(pointsize);
        rightSetBoard.setX(centerX+margin);
        rightSetBoard.setY(2*margin+pointsize-setsize);
        rightSetBoard.setHeight(setsize);
        rightSetBoard.setWidth(setsize);


    }

    public void setSetTextSize(int size) {
        leftSetBoard.setTextSize(size);
        rightSetBoard.setTextSize(size);
    }

    public void setPointTextSize(int size) {
        leftPointBoard.setTextSize(size);
        rightPointBoard.setTextSize(size);
    }

    public void setLeftSetCount(int value) {
        leftSetCount = value;
        leftSetBoard.setText(String.valueOf(value));
    }

    public void setLeftPointCount(int value) {
        leftPointCount = value;
        leftPointBoard.setText(String.valueOf(value));
    }

    public void setRightSetCount(int value) {
        rightSetCount = value;
        rightSetBoard.setText(String.valueOf(value));
    }

    public void setRightPointCount(int value) {
        rightPointCount = value;
        rightPointBoard.setText(String.valueOf(value));
    }

    public int getLeftPointCount() {
        return leftPointCount;
    }

    public int getLeftSetCount() {
        return leftSetCount;
    }

    public int getRightPointCount() {
        return rightPointCount;
    }

    public int getRightSetCount() {
        return rightSetCount;
    }

}
