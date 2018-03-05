package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uskysd.smartvolley.InputListener;
import uskysd.smartvolley.R.color;

public class GraphicSet extends AdjustableDrawable {
	
	private static final String TAG = GraphicSet.class.getSimpleName();

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
    private int COURT_COLOR = Color.parseColor(String.valueOf(color.court));
	private int LINE_COLOR = Color.parseColor(String.valueOf(color.court_line));
	private int LINE_WIDTH = 5;


	public enum Area { LEFT_FRONT, LEFT_BACK, LEFT_SERVICE, LEFT_BENCH, LEFT_FARSIDE,
		RIGHT_FRONT, RIGHT_BACK, RIGHT_SERCICE, RIGHT_BENCH, RIGHT_FARSIDE}
	public enum Side {
		LEFT_COURT, RIGHT_COURT
	}
	public enum Position {
		BACK_LEFT, FRONT_LEFT, FRONT_CENTER, FRONT_RIGHT, BACK_RIGHT, BACK_CENTER
	}

	//Player tokens
    PlayerToken[] leftSidePlayers;
	private PlayerToken[] rightSidePlayers;

    //Markers
    private ArrayList<PlayerToken> playerMarkers = new ArrayList<PlayerToken>();
    private ArrayList<CrossMarker> crossMarkers = new ArrayList<CrossMarker>();
    private ArrayList<ArrowMarker> arrowMarkers = new ArrayList<ArrowMarker>();

    //Cash touch information
    private Token touchedToken = null;
    private Area touchStartArea = null;



    //Input Listener
    private InputListener inputListener;
	
	public GraphicSet() {

		super();
		initToken();
		Log.d(TAG, "Instantiated");
	}

    public void setInputListener(InputListener listener) {
        this.inputListener = listener;
    }
	
	
	public void initToken() {
		Log.d(TAG, "Init tokens");
		//Init area
		leftArea = new RectangleArea(0, 0, 0, 0, COURT_COLOR);
		rightArea = new RectangleArea(0, 0, 0, 0, COURT_COLOR);
		
		//Init player tokens
		leftSidePlayers = new PlayerToken[6];
		rightSidePlayers = new PlayerToken[6];
		List<Position> positions = new ArrayList<Position>(Arrays.asList(Position.values()));
		for (int i=0; i<6; i++) {
//			leftSidePlayers[i] = new PlayerToken(Side.LEFT_COURT, positions.get(i));
//			rightSidePlayers[i] = new PlayerToken(Side.RIGHT_COURT, positions.get(i));
			leftSidePlayers[i].setNumber(i);
			rightSidePlayers[i].setNumber(i);
		}

        //Set markers for test
        ArrowMarker am = new ArrowMarker(0, 0, 100, 200);
        arrowMarkers.add(am);
        CrossMarker cm = new CrossMarker(100, 200);
        crossMarkers.add(cm);

		
//		initPlayerTokenLocation();
		
	}
	
	public void initLayout(int width, int height) {
		adjustLayout(width, height);
		initPlayerTokenLocation();
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
        if (side==Side.LEFT_COURT) {
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
                    location.y = farY;
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


	public void initPlayerTokenLocation() {
		Log.d(TAG, "Init player token's location");
		int margin = (int) (0.15*centerX);
		int leftFrontX = centerX - margin;
		int leftBackX = leftEndX + margin;
		int rightFrontX = centerX + margin;
		int rightBackX = rightEndX - margin;
		int centerY = (int) (0.5*(nearSideY + farSideY));
		int farY = farSideY + margin;
		int nearY = nearSideY - margin;
		
		//Left side
		for (PlayerToken pt: leftSidePlayers) {
			switch (pt.getPosition()) {
			case BACK_LEFT:
				pt.setX(leftBackX);
				pt.setY(farY);
				break;
			case FRONT_LEFT:
				pt.setX(leftFrontX);
				pt.setY(farY);
				break;
			case FRONT_CENTER:
				pt.setX(leftFrontX);
				pt.setY(centerY);
				break;
			case FRONT_RIGHT:
				pt.setX(leftFrontX);
				pt.setY(nearY);
				break;
			case BACK_RIGHT:
				pt.setX(leftBackX);
				pt.setY(nearY);
				break;
			case BACK_CENTER:
				pt.setX(leftBackX);
				pt.setY(centerY);
				break;
			}
		}
		//Right side
		for (PlayerToken pt: rightSidePlayers) {
			switch (pt.getPosition()) {
			case BACK_LEFT:
				pt.setX(rightBackX);
				pt.setY(nearY);
				break;
			case FRONT_LEFT:
				pt.setX(rightFrontX);
				pt.setY(nearY);
				break;
			case FRONT_CENTER:
				pt.setX(rightFrontX);
				pt.setY(centerY);
				break;
			case FRONT_RIGHT:
				pt.setX(rightFrontX);
				pt.setY(farY);
				break;
			case BACK_RIGHT:
				pt.setX(rightBackX);
				pt.setY(farY);
				break;
			case BACK_CENTER:
				pt.setX(rightBackX);
				pt.setY(centerY);
				break;
			}
		}
	}
	
	public PlayerToken getPlayerToken(Side side, Position position) {
		/*
        switch (side) {
		case LEFT_COURT:
			for (PlayerToken pt: leftSidePlayers) {
				if (pt.getPosition()==position) {
					return pt;
				}
			}
			Log.d(TAG, "Player token not found");
			return null;
			
		case RIGHT_COURT:
			for (PlayerToken pt: rightSidePlayers) {
				if (pt.getPosition()==position) {
					return pt;
				}
			}
			Log.d(TAG, "Player token not found");
			return null;
		default:
			Log.d(TAG, "Player token not found");
			return null;
		}
		*/
        return null;
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
		
		//Adjust player token size
		PlayerToken.setRadius((int) (0.05*courtHeight));

        //Adjust cross marker size
        CrossMarker.setSize((int) 0.01 * courtHeight);

        //Adjust arrow marker head size
        ArrowMarker.setHeadHeight((float) 0.01*courtHeight);
        ArrowMarker.setHeadWidth((float) 0.008*courtHeight);


		
		
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
		
		//Draw player tokens
		for (PlayerToken pt: leftSidePlayers) {
			pt.draw(canvas);
		}
		for (PlayerToken pt: rightSidePlayers) {
			pt.draw(canvas);
		}

        //Draw cross markers
        for (CrossMarker cm: crossMarkers) {
            cm.draw(canvas);
        }

        //Draw arrow markers
        for (ArrowMarker am: arrowMarkers) {
            am.draw(canvas);
        }
		
		
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
	
	public void handleActionDown(int x, int y) {
		touchedToken = null;
        if (x < centerX) {
			for (PlayerToken pt: leftSidePlayers) {
				if (pt.checkTouched(x, y)==true) {
			        touchedToken = pt;
					//TODO set last touched date-time to the token
					break;
				}
			}
		} else {
			for (PlayerToken pt: rightSidePlayers) {
				if (pt.checkTouched(x, y)==true) {
                    touchedToken = pt;
					//TODO set last touched date-time to the token
					break;
				}
			}
		}
        if (touchedToken!=null) {
            try {
                inputListener.onPlayerTouched(((PlayerToken) touchedToken).getPlayerId(), x, y);
            } catch (ClassCastException e) {
                Log.d(TAG, "Touched token is not player token");
            }
        } else {
            touchStartArea = checkArea(x, y);
        }


	}
	
	public void handleActionMove_bak(int x, int y) {
		PlayerToken pt;
		for (int i=0; i<6; i++) {
			pt = leftSidePlayers[i];
			if (pt.isTouched()) {
				pt.setLocation(x, y);
			}
			pt = rightSidePlayers[i];
			if (pt.isTouched()) {
				pt.setLocation(x, y);
			}
		}
	}

    public void handleActionMove(int x, int y) {
        if (touchedToken != null) {
            try {
                PlayerToken touchedPlayer = (PlayerToken) touchedToken;
                touchedPlayer.setLocation(x, y);
            } catch (ClassCastException e) {
                throw new RuntimeException(e);
            }
        }
    }
	

	public void handleActionUp(int x, int y) {
		//Touch was released
		for (int i=0; i<6; i++) {
			leftSidePlayers[i].setTouched(false);
			rightSidePlayers[i].setTouched(false);
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
