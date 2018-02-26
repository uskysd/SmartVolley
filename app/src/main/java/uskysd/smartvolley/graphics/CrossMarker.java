package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


/**
 * Created by yusukeyohishida on 2014/07/20.
 */
public class CrossMarker extends Token {

    private static int size = 10;
    private static int lineWidth = 1;
    //private static int color = Color.parseColor(String.valueOf(R.color.cross_marker));
    private static int color = Color.DKGRAY;


    public CrossMarker(int x, int y) {
        super(x, y);
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);
        int dx = (int) 0.5 * size;
        //Draw cross
        canvas.drawLine(getX()-dx, getY()-dx, getX()+dx, getY()+dx, paint);
        canvas.drawLine(getX()-dx, getY()+dx, getX()+dx, getY()-dx, paint);
    }


    @Override
    public void adjustLayout(int width, int height) {

    }

    public boolean checkTouched(int x, int y) {
        int dx = (int) 0.5 * size;
        if (x > getX()-dx && x < getX()+dx && y > getY()-dx && y < getY()+dx) {
            setTouched(true);
        } else {
            setTouched(false);
        }
        return isTouched();

    }

    public static void setSize(int size) {
        CrossMarker.size = size;
    }

    public static int getSize() {
        return size;
    }

    public static void setLineWidth(int lineWidth) {
        CrossMarker.lineWidth = lineWidth;
    }

    public static int getLineWidth() {
        return lineWidth;
    }
}
