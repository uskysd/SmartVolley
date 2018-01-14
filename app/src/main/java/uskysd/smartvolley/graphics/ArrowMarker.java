package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import uskysd.smartvolley.R;

import static java.lang.Math.sqrt;


/**
 * Created by yusukeyohishida on 2014/07/20.
 */
public class ArrowMarker extends Token {

    private int x;
    private int y;
    private int stopX;
    private int stopY;
    private int color = Color.parseColor(String.valueOf(R.color.arrow_marker));
    private static int lineWidth = 2;
    private static float headHeight = 10;
    private static float headWidth = 8;
    private boolean headClosed = false;
    private Token startToken = null;
    private Token stopToken = null;

    public ArrowMarker(int startX, int startY, int stopX, int stopY) {
        super(startX, startY);
        this.stopX = stopX;
        this.stopY = stopY;
    }




    @Override
    public void update() {
        if (startToken!=null) {
            setStartX(startToken.getX());
            setStartY(startToken.getY());
        }
        if (stopToken!=null) {
            setStopX(stopToken.getX());
            setStopY(stopToken.getY());
        }

    }


    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);

        //Draw line
        canvas.drawLine(x, y, stopX, stopY, paint);

        //Draw arrow head
        Path path = new Path();
        float dx = stopX - x;
        float dy = stopY - y;
        float length = (float) sqrt(dx*dx + dy*dy);
        float p1_x = (float) (stopX - headHeight/length*dx + 0.5*headWidth/length*dy);
        float p1_y = (float) (stopY - headHeight/length*dy - 0.5*headWidth/length*dx);
        float p2_x = (float) (stopX -headWidth/length*dx - 0.5*headWidth/length*dy);
        float p2_y = (float) (stopY - headHeight/length*dx + 0.5*headWidth/length*dx);
        path.moveTo(p1_x, p1_y);
        path.lineTo(stopX, stopY);
        path.lineTo(p2_x, p2_y);
        if (headClosed==true) {
            path.close();
        }
        canvas.drawPath(path, paint);
    }


    @Override
    public void adjustLayout(int width, int height) {

    }

    @Override
    public void setLocation(int x, int y) {
        setTarget(x, y);
    }

    public void setTarget(int x, int y) {
        this.stopX = x;
        this.stopY = y;
    }

    public void setStartX(int startX) {
        this.x = startX;
    }

    public int getStartX() {
        return x;
    }

    public void setStartY(int startY) {
        this.y = startY;
    }

    public int getStartY() {
        return y;
    }

    public void setStopX(int stopX) {
        this.stopX = stopX;
    }

    public int getStopX() {
        return stopX;
    }

    public void setStopY(int stopY) {
        this.stopY = stopY;
    }

    public int getStopY() {
        return stopY;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public static void setHeadHeight(float headHeight) {
        ArrowMarker.headHeight = headHeight;
    }

    public static float getHeadHeight() {
        return headHeight;
    }

    public static void setHeadWidth(float headWidth) {
        ArrowMarker.headWidth = headWidth;
    }

    public static float getHeadWidth() {
        return headWidth;
    }

    public static void setLineWidth(int lineWidth) {
        ArrowMarker.lineWidth = lineWidth;
    }

    public static int getLineWidth() {
        return lineWidth;
    }

    public void setHeadClosed(boolean headClosed) {
        this.headClosed = headClosed;
    }

    public boolean hasHeadClosed() {
        return headClosed;
    }

    public Token getStopToken() {
        return stopToken;
    }

    public void setStopToken(Token stopToken) {
        this.stopToken = stopToken;
    }

    public Token getStartToken() {
        return startToken;
    }

    public void setStartToken(Token startToken) {
        this.startToken = startToken;
    }
}
