package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import uskysd.smartvolley.Position;

public class Court extends AdjustableDrawable {

	private static final String TAG = Court.class.getSimpleName();

	private RectangleArea leftArea;
	private RectangleArea rightArea;
	private static int courtWidth = 0;
	private static int courtHeight = 0;
	private static int centerX = 0;
	private static int leftAttackX = 0;
	private static int rightAttackX = 0;
	private static int leftEndX = 0;
	private static int rightEndX = 0;
	private static int nearSideY = 0;
	private static int farSideY = 0;
    //private static int COURT_COLOR = Color.parseColor(String.valueOf(R.color.court));
	//private static int LINE_COLOR = Color.parseColor(String.valueOf(R.color.court_line));
	private static int COURT_COLOR = Color.RED;
	private static int LINE_COLOR = Color.BLUE;

	private static int LINE_WIDTH = 5;


	public enum Area { LEFT_FRONT, LEFT_BACK, LEFT_SERVICE, LEFT_BENCH, LEFT_FARSIDE,
		RIGHT_FRONT, RIGHT_BACK, RIGHT_SERCICE, RIGHT_BENCH, RIGHT_FARSIDE}
	public enum Side {
		LEFT_COURT, RIGHT_COURT
	}
	/*
	public enum Position {
		BACK_LEFT, FRONT_LEFT, FRONT_CENTER, FRONT_RIGHT, BACK_RIGHT, BACK_CENTER
	}
	*/

	public Court() {

		super();
		initToken();
		Log.d(TAG, "Instantiated");
	}
	
	
	public void initToken() {
		Log.d(TAG, "Init tokens");
		//Init area
		leftArea = new RectangleArea(0, 0, 0, 0, COURT_COLOR);
		rightArea = new RectangleArea(0, 0, 0, 0, COURT_COLOR);

	}
	
	public void initLayout(int width, int height) {
		adjustLayout(width, height);
	}

    public Point positionToLocation(Side side, Position position) {
        int margin = (int) (0.15*centerX);
        int leftFrontX = centerX - margin;
        int leftBackX = leftEndX + margin;
        int rightFrontX = centerX + margin;
        int rightBackX = rightEndX - margin;
        int centerY = (int) (0.5*(nearSideY + farSideY));
        int farY = farSideY + margin;
        int nearY = nearSideY - margin;

        Point location = new Point();
        if (side== Side.LEFT_COURT) {
            switch (position) {
                case BACK_LEFT:
                    location.x = leftBackX;
                    location.y = farY;
                    break;
                case FRONT_LEFT:
                    location.x = leftFrontX;
                    location.y = farY;
                    break;
                case FRONT_CENTER:
                    location.x = leftFrontX;
                    location.y = centerY;
                    break;
                case FRONT_RIGHT:
                    location.x = leftFrontX;
                    location.y = nearY;
                    break;
                case BACK_RIGHT:
                    location.x = leftBackX;
                    location.y = nearY;
                    break;
                case BACK_CENTER:
                    location.x = leftBackX;
                    location.y = centerY;
                    break;
            }
        } else {
            switch (position) {
                case BACK_LEFT:
                    location.x = rightBackX;
                    location.y = nearY;
                    break;
                case FRONT_LEFT:
                    location.x = rightFrontX;
                    location.y = nearY;
                    break;
                case FRONT_CENTER:
                    location.x = rightFrontX;
                    location.y = centerY;
                    break;
                case FRONT_RIGHT:
                    location.x = rightFrontX;
                    location.y = farY;
                    break;
                case BACK_RIGHT:
                    location.x = rightBackX;
                    location.y = farY;
                    break;
                case BACK_CENTER:
                    location.x = rightBackX;
                    location.y = centerY;
                    break;

            }
        }
        return location;
    }



	

	
	@Override
	public void adjustLayout(int width, int height) {
		Log.d(TAG, "Adjusting layout size to the screen");
		courtWidth = width;
		courtHeight = height;
		leftEndX = (int) (0.1*width);
		rightEndX = (int) (0.9*width);
		nearSideY = (int) (0.9*height);
		farSideY = (int) (0.1*height);
		
		//Adjust center line
		centerX = (int) (0.5*width);
		
		//Adjust attack line
		leftAttackX = (int) (0.37*width);
		rightAttackX = (int) (0.63*width);
		
		//Adjust court size
		int courtWidth = centerX - leftEndX;
		int courtHeight = nearSideY - farSideY;
		
		//Adjust left area
		leftArea.setX(leftEndX);
		leftArea.setWidth(courtWidth);
		leftArea.setY(farSideY);
		leftArea.setHeight(courtHeight);
		
		//Adjust right area
		rightArea.setX(centerX);
		rightArea.setWidth(courtWidth);
		rightArea.setY(farSideY);
		rightArea.setHeight(courtHeight);
	}


	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

        //Draw court
		int height = canvas.getHeight();
		int width = canvas.getWidth();
//		adjustLayout(width, height);
		leftArea.draw(canvas);
		rightArea.draw(canvas);
		Paint paint = new Paint();
		paint.setColor(LINE_COLOR);
		paint.setStrokeWidth(LINE_WIDTH);
		canvas.drawLine(centerX, (int) (0.05*height), centerX, (int) (0.95*height), paint);
		canvas.drawLine(leftAttackX, (int) (0.1*height), leftAttackX, (int) (0.9*height), paint);
		canvas.drawLine(rightAttackX, (int) (0.1*height), rightAttackX, (int) (0.9*height), paint);
		

		
	}
	

	public Area checkArea(int x, int y) {
		//Return area of the point
		if (y < farSideY) {
			if (x < centerX) {
				return Area.LEFT_FARSIDE;
			} else {
				return Area.RIGHT_FARSIDE;
			}
		} else if (y < nearSideY) {
			if (x < leftEndX) {
				return Area.LEFT_SERVICE;
			} else if (x < leftAttackX) {
				return Area.LEFT_BACK;
			} else if (x < centerX) {
				return Area.LEFT_FRONT;
			} else if (x < rightAttackX) {
				return Area.RIGHT_FRONT;
			} else if (x < rightEndX) {
				return Area.RIGHT_BACK;
			} else {
				return Area.RIGHT_SERCICE;
			}
		} else {
			if (x < centerX) {
				return Area.LEFT_BENCH;
			} else {
				return Area.RIGHT_BENCH;
			}
		}
		
	}
	

	public static int getCenterX() {
		return centerX;
	}


	public static int getCourtWidth() {
		return courtWidth;
	}


	public static int getCourtHeight() {
		return courtHeight;
	}


	public static int getLeftAttackX() {
		return leftAttackX;
	}


	public static int getRightAttackX() {
		return rightAttackX;
	}


	public static int getLeftEndX() {
		return leftEndX;
	}


	public static int getRightEndX() {
		return rightEndX;
	}


	public static int getNearSideY() {
		return nearSideY;
	}


	public static int getFarSideY() {
		return farSideY;
	}






}
