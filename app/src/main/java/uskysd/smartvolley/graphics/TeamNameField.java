package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;

public class TeamNameField extends AdjustableDrawable {


    private static final String TAG = TeamNameField.class.getSimpleName();

    private RectangleArea fieldA;
    private RectangleArea fieldB;
    private int fieldColor = Color.WHITE;
    private int textColor = Color.BLACK;

    public TeamNameField() {
        fieldA = new RectangleArea(0, 0, 0, 0, fieldColor);
        fieldB = new RectangleArea(0, 0 ,0, 0, fieldColor);
        fieldA.setText("A");
        fieldB.setText("B");
        //fieldA.setTextColor(textColor);
        //fieldB.setTextColor(textColor);
        Log.d(TAG, "Created TeamNameField");
        Log.d(TAG, "Field A Text: "+fieldA.getText());

    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        fieldA.draw(canvas);
        fieldB.draw(canvas);

    }

    @Override
    public void adjustLayout(int width, int height) {
        // Adjust layout size
        int margin = (int) (0.01 * width);
        int outerOffset = (int) (0.1*width);
        int innerOffset = (int) (0.15*width);
        int fieldHeight = (int) (0.06 * height);
        int fieldWidth = (int) (0.5*width-outerOffset-innerOffset);
        int textSize = (int)(0.6*fieldHeight);

        int centerX = (int) (0.5 * width);

        fieldA.setX(outerOffset);
        fieldA.setY(2*margin);
        fieldA.setHeight(fieldHeight);
        fieldA.setWidth(fieldWidth);
        fieldA.setTextSize(textSize);
        fieldB.setX(centerX+innerOffset);
        fieldB.setY(2*margin);
        fieldB.setHeight(fieldHeight);
        fieldB.setWidth(fieldWidth);
        fieldB.setTextSize(textSize);
    }


    public void setTeamNameA(String name) {
        fieldA.setText(name);
    }

    public void setTeamNameB(String name) {
        fieldB.setText(name);
    }
}
