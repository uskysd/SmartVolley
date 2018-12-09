package uskysd.smartvolley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uskysd.smartvolley.data.Match;
import uskysd.smartvolley.data.Player;
import uskysd.smartvolley.data.PlayerEntry;
import uskysd.smartvolley.graphics.ArrowMarker;
import uskysd.smartvolley.graphics.Court;
import uskysd.smartvolley.graphics.CrossMarker;
import uskysd.smartvolley.graphics.PlayerToken;
import uskysd.smartvolley.graphics.RectangleArea;
import uskysd.smartvolley.graphics.RotationBoard;
import uskysd.smartvolley.graphics.Scoreboard;
import uskysd.smartvolley.graphics.TeamNameField;
import uskysd.smartvolley.graphics.Token;

public class MatchView extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = MatchView.class.getSimpleName();
	private int COURT_COLOR = getResources().getColor(R.color.orange);
	private int BACKGROUND_COLOR = getResources().getColor(R.color.green_apple);
	private int ROTATION_COLOR = getResources().getColor(R.color.light_cyan);
	private static int LONG_TOUCH_TIME = 1000; //msec
	private MatchThread thread;

    private InputListener inputListener;

    //Tokens
//	private GraphicSet graphic;
	private Court court;
	private Scoreboard scoreboard;
	private TeamNameField teamNameField;

    private PlayerToken[] leftPlayerTokens;
    private PlayerToken[] rightPlayerTokens;
    //private ArrayList<PlayerToken> playerMakers = new ArrayList<PlayerToken>();
    private ArrayList<CrossMarker> crossMarkers = new ArrayList<CrossMarker>();
    private ArrayList<ArrowMarker> arrowMarkers = new ArrayList<ArrowMarker>();
    private RotationBoard leftRotation;
    private RotationBoard rightRotation;
    private RectangleArea analysisButton;


    //Cash touched token
    private Token touchedToken;
    private Point touchStartPoint;
    private DateTime touchStartTime;

    //Judge swipe
    private static int SWIPE_START_DISTANCE_RATIO = (int) 0.5;
    private boolean swiping = false;

    private String avgFpsString;
	private final DecimalFormat df = new DecimalFormat("0.##");

	public MatchView(Context context) {
		super(context);
		initialize(context, null, 0);

	}

	public MatchView(Context context, AttributeSet attrs) {
	    // This constructor is for creating view through XML
	    super(context, attrs, 0);
	    initialize(context, attrs, 0);
    }

    public MatchView(Context context, AttributeSet attrs, int defStyle) {
	    // This constructor is for creating view through XML
        super(context, attrs, defStyle);
        initialize(context, attrs, 0);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        getHolder().addCallback(this);
        initView(context);

        //Set input listener
        setInputListener(new InputListener());


        //create thread
        thread = new MatchThread(getHolder(), this);
        setFocusable(true);

    }

    public void deactivate() {
	    // Interrupt thread
	    thread.interrupt();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //court.initLayout(widthMeasureSpec, heightMeasureSpec);

        //PlayerToken.setRadius((int) (0.05*heightMeasureSpec));
        //PlayerToken.setTextSize((int) (0.05*heightMeasureSpec));

        //updatePlayerTokenLocation();
    }


    public void loadStartingPositions(Match match) {
        //Load match info


        //Load player entries to team A
        for (PlayerEntry entry: match.getPlayerEntriesToTeamA()) {
            PlayerToken pt = getPlayerToken(Court.Side.LEFT_COURT, entry.getStartingPosition());
            if (pt!=null && entry.getNumber()!=null) {
                pt.setNumber(entry.getNumber());
                pt.setPlayerId(entry.getPlayer().getId());

            }
        }

        //Load player entries to team B
        for (PlayerEntry entry: match.getPlayerEntriesToTeamB()) {
            PlayerToken pt = getPlayerToken(Court.Side.RIGHT_COURT, entry.getStartingPosition());

            if (pt!=null && entry.getNumber()!=null) {
                pt.setNumber(entry.getNumber());
                pt.setPlayerId(entry.getPlayer().getId());
            }
        }


    }

    public void loadPlayer(Court.Side side, Position position, Player player) {
	    PlayerToken pt = getPlayerToken(side, position);
	    if (pt!=null) {
	        pt.setNumber(player.getUniformNumber());
	        pt.setPlayerId(player.getId());
        } else {
	        throw new RuntimeException("Player token not found for the position: "+position.toString());
        }
    }

    public void setLeftPointCount(int value) {
	    scoreboard.setLeftPointCount(value);
    }

    public void setRightPointCount(int value) {
	    scoreboard.setRightPointCount(value);
    }

    public void setLeftSetCount(int value) {
	    scoreboard.setLeftSetCount(value);
    }

    public void setRightSetCount(int value) {
	    scoreboard.setRightSetCount(value);
    }

    public void setLeftRotation(int rotation) {
	    leftRotation.setRotation(rotation);

	    // Rotate players
        for (PlayerToken pt: leftPlayerTokens) {
            pt.setRotation(rotation);
        }
    }

    public void setRightRotation(int rotation) {
	    rightRotation.setRotation(rotation);

        // Rotate players
        for (PlayerToken pt: rightPlayerTokens) {
            pt.setRotation(rotation);
        }
    }
    
    public void rotateLeft() {
	    int current = leftRotation.getRotation();
	    setLeftRotation(current==6 ? 1:current+1);
	    updatePlayerTokenLocation();
    }
    public void rotateRight() {
        int current = rightRotation.getRotation();
        setRightRotation(current==6 ? 1:current+1);
        updatePlayerTokenLocation();
    }

    public int getLeftRotation() {
	    return leftRotation.getRotation();
    }

    public int getRightRotation() {
	    return rightRotation.getRotation();
    }

    /* Now using common enum for Position
    public Court.Position startingPositionToCourtPosition(Position startingPosition) {
        Court.Position courtPosition = null;
        switch (startingPosition) {
            case BACK_LEFT:
                courtPosition = Position.BACK_LEFT;
                break;
            case FRONT_LEFT:
                courtPosition = Position.FRONT_LEFT;
                break;
            case FRONT_CENTER:
                courtPosition = Position.FRONT_CENTER;
                break;
            case FRONT_RIGHT:
                courtPosition = Position.FRONT_RIGHT;
                break;
            case BACK_RIGHT:
                courtPosition = Position.BACK_RIGHT;
                break;
            case BACK_CENTER:
                courtPosition = Position.BACK_CENTER;
                break;
        }
        return courtPosition;
    }
    */


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface created");
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    Log.d(TAG, "Surface destroyed");
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// TODO try again shutting down the thread
                Log.d(TAG, "Shutting down the thread");
                thread.interrupt();

			}
		}
	}






    @Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
//			graphic.handleActionDown((int) event.getX(), (int) event.getY());
			handleActionDown((int) event.getX(), (int) event.getY());
            break;
		case MotionEvent.ACTION_MOVE:
//			graphic.handleActionMove((int) event.getX(), (int) event.getY());
			handleActionMove((int) event.getX(), (int) event.getY());
            break;
		case MotionEvent.ACTION_UP:
//			graphic.handleActionUp((int) event.getX(), (int) event.getY());
			handleActionUp((int) event.getX(), (int) event.getY());
            break;
		default:
			break;
		}
		return true;
	}

    public void handleActionDown(int x, int y) {
        //called when the display is touched
        Log.d(TAG, "Touch event: action down");
        touchStartTime = DateTime.now();
        touchStartPoint = new Point(x, y);

        // Check player token touched
        Token touched = checkTouchedToken(x, y);
        if (touched!=null) {
            try {
                PlayerToken pt = (PlayerToken) touched;
                inputListener.onPlayerTouched(pt.getPlayerId(), x, y);
            } catch (ClassCastException e) {
                Log.d(TAG, "Touched token is not player token");
            }

        }

        // Check scoreboard touched
        if (scoreboard.checkLeftBoardTouched(x, y)) {
            inputListener.onScoreBoardLeftTouched(x, y);
        }
        if (scoreboard.checkRightBoardTouched(x, y)) {
            inputListener.onScoreBoardRightTouched(x, y);
        }

        // Check rotation boards
        if (leftRotation.checkTouched(x, y)) {
            inputListener.onRotationBoardLeftTouched(x, y);
        }
        if (rightRotation.checkTouched(x, y)) {
            inputListener.onRotationBoardRightTouched(x, y);
        }

        // Check analysis button touched
        if (analysisButton.checkTouched(x, y)) {
            inputListener.onAnalysisButtonTouched(x, y);
        }

    }

    public void handleActionMove(int x, int y) {
        //called while swiping the display
        Log.d(TAG, "Touch event: action move");
        PlayerToken pt = getTouchedPlayerToken();

        if (pt!=null) {
            int dx = x - touchStartPoint.x;
            int dy = y - touchStartPoint.y;
            if (dx*dx+dy*dy > PlayerToken.getRadius()*SWIPE_START_DISTANCE_RATIO) {
                setSwiping(true);
            }
            if (isSwiping()==true) {
                //Update location
                pt.setLocation(x, y);
            } else if (touchStartTime!=null) {
                if (DateTime.now().getMillis() - touchStartTime.getMillis() > LONG_TOUCH_TIME) {
                    //Player long touched
                    inputListener.onPlayerLongTouched(pt.getPlayerId(), x, y);
                }

            }
        } else {
            // Do nothing
        }

    }

    public void handleActionUp(int x, int y) {
        //called when the touch is released
        Log.d(TAG, "Touch event: action up");
        setSwiping(false);
        touchedToken = null;
        touchStartPoint = null;
        touchStartTime = null;
    }

    public PlayerToken getTouchedPlayerToken() {
        try {
            return (PlayerToken) touchedToken;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public Token checkTouchedToken(int x, int y) {
        //Check Player Token
        if (x < court.getCenterX()) {
            for (PlayerToken pt: leftPlayerTokens) {
                if (pt.checkTouched(x, y)==true) {
                    touchedToken = pt;
                }
            }
        } else {
            for (PlayerToken pt: rightPlayerTokens) {
                if (pt.checkTouched(x, y)==true) {
                    touchedToken = pt;
                }
            }
        }

        return touchedToken;
    }


	/*
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for (int i=0; i < playerTokens.length; i++) {
				if (playerTokens[i] != null) {
					playerTokens[i].handleActionDown((int)event.getX(), (int)event.getY());
				}
			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			for (PlayerToken pt: playerTokens) {
				if (pt != null && pt.isTouched()) {
					pt.setX((int)event.getX());
					pt.setY((int)event.getY());
				}
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			//touch was released
			for (PlayerToken pt: playerTokens) {
				if (pt.isTouched()) {
					pt.setTouched(false);
				}
			}
		}
		
		return true;
	}
	*/


	public void initView(Context context) {
		Log.d(TAG, "Initializeing view");

		//Init tokens
//		graphic = new GraphicSet();
        court = new Court();

        scoreboard = new Scoreboard();
        teamNameField = new TeamNameField();
        //teamNameField.setTeamNameA("A");
        //teamNameField.setTeamNameB("B");
        leftPlayerTokens = new PlayerToken[6];
        rightPlayerTokens = new PlayerToken[6];

        leftRotation = new RotationBoard(0, 0, 0, 0, ROTATION_COLOR);
        rightRotation = new RotationBoard(0, 0,0, 0, ROTATION_COLOR);

        analysisButton = new RectangleArea(0, 0, 0, 0, Color.TRANSPARENT);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icon_analysis);
        analysisButton.setImage(bm);

        //List<Position> positions = Arrays.asList(Position.values());
        List<Position> positions = Arrays.asList(
                Position.FRONT_LEFT, Position.FRONT_CENTER, Position.FRONT_RIGHT,
                Position.BACK_LEFT, Position.BACK_CENTER, Position.BACK_RIGHT);
        for (int i=0; i<6; i++) {
            leftPlayerTokens[i] = new PlayerToken(Court.Side.LEFT_COURT, positions.get(i));
            rightPlayerTokens[i] = new PlayerToken(Court.Side.RIGHT_COURT, positions.get(i));
            leftPlayerTokens[i].setNumber(i);
            rightPlayerTokens[i].setNumber(i);
            //Set location

        }




//        graphic.setInputListener(listener);

		//Get screen size

		int width = 0;
		int height = 0;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size); //Only for API LEVEL >= 13
			width = (int) (size.x * 0.75);
			height = size.y;
		} else {
			width = display.getWidth(); //deprecated
			height = display.getHeight(); //deprecated
		}

		// Adjust layout size

        court.initLayout(width, height);
		scoreboard.adjustLayout(width, height);
		teamNameField.adjustLayout(width, height);


		// Adjust rotation boards
        int rotsize = (int) (0.1*height);
        int rotmargin = (int) (0.01*height);
        int rottextsize = (int) (0.8*rotsize);
        leftRotation.updateCoordinate(rotmargin, rotmargin, rotsize, rotsize);
        rightRotation.updateCoordinate(width-rotmargin-rotsize, rotmargin, rotsize, rotsize);
        leftRotation.setTextSize(rottextsize);
        rightRotation.setTextSize(rottextsize);

        // Adjust player token size
        PlayerToken.setRadius((int) (0.05*height));
        PlayerToken.setTextSize((int) (0.05*height));

        updatePlayerTokenLocation();
//		graphic.initLayout(width, height);


        // Adjust analysis button
        int margin = (int) (0.01*height);
        int buttonsize = (int) (0.1*height);
        analysisButton.updateCoordinate(width-buttonsize-margin, height-buttonsize-margin,
                buttonsize, buttonsize);





	}

    public void updatePlayerTokenLocation() {
        //Init player token location
        for (PlayerToken pt: leftPlayerTokens) {
            Point location = court.positionToLocation(pt.getSide(), pt.getPosition());
            pt.setLocation(location.x, location.y);

        }
        for (PlayerToken pt: rightPlayerTokens) {
            Point location = court.positionToLocation(pt.getSide(), pt.getPosition());
            pt.setLocation(location.x, location.y);
        }
    }



	public void update() {
		// update graphics information

        court.update();

        for (PlayerToken pt: leftPlayerTokens) {
            pt.update();
        }
        for (PlayerToken pt: rightPlayerTokens) {
            pt.update();
        }

        for (ArrowMarker am: arrowMarkers) {
            am.update();
        }

        for (CrossMarker cm: crossMarkers) {
            cm.update();
        }

        //update token location
        //updatePlayerTokenLocation();


	}

	public void render(Canvas canvas) {

		canvas.drawColor(BACKGROUND_COLOR);

//		graphic.draw(canvas);

        //Draw court
        court.draw(canvas);

        // Draw scoreboard
        scoreboard.draw(canvas);

        // Draw team name field
        teamNameField.draw(canvas);
        //teamA.draw(canvas);
        //teamB.draw(canvas);


        // Draw rotation boards
        leftRotation.draw(canvas);
        rightRotation.draw(canvas);

        // Draw analysis button
        analysisButton.draw(canvas);


        //Draw player marker
        /*
        for (PlayerToken pm: playerMakers) {
            pm.draw(canvas);
        }
        */

        //Draw cross markers
        for (CrossMarker cm: crossMarkers) {
            cm.draw(canvas);
        }
        //Draw arrow markers
        for (ArrowMarker am: arrowMarkers) {
            am.draw(canvas);
        }

        //Draw players
        for (PlayerToken pt: leftPlayerTokens) {
            pt.draw(canvas);
        }
        for (PlayerToken pt: rightPlayerTokens) {
            pt.draw(canvas);
        }

	}


    public void setTeamA(String name) {
	    teamNameField.setTeamNameA(name);
    }

    public void setTeamB(String name) {
	    teamNameField.setTeamNameB(name);
    }

    public PlayerToken getPlayerToken(Court.Side side, Position position) {
        switch (side) {
            case LEFT_COURT:
                for (PlayerToken pt: leftPlayerTokens) {
                    if (pt.getPosition()==position) {
                        return pt;
                    }
                }
                break;
            case RIGHT_COURT:
                for (PlayerToken pt: rightPlayerTokens) {
                    if (pt.getPosition()==position) {
                        return pt;
                    }
                }
                break;
        }
        return null;
    }

    public List<PlayerToken> getPlayerTokens() {
	    ArrayList<PlayerToken> tokens = new ArrayList<PlayerToken>(Arrays.asList(leftPlayerTokens));
	    tokens.addAll(Arrays.asList(rightPlayerTokens));
	    return tokens;
    }

    public void addArrowMarker(int startX, int startY, int stopX, int stopY, boolean swiping) {
        ArrowMarker marker = new ArrowMarker(startX, startY, stopX, stopY);
        this.arrowMarkers.add(marker);
        touchedToken = (Token) marker;
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public InputListener getInputListener() {
        return inputListener;
    }

    public void setAvgFps(double avgFps) {
		this.avgFpsString = "FPS: " + df.format(avgFps);
	}

    public void setSwiping(boolean swiping) {
        this.swiping = swiping;
    }

    public boolean isSwiping() {
        return swiping;
    }

    public class InputListener2 {

        public InputListener2() {


    }
        public void onPlayerSwiped(int playerId, int startX, int startY, int endX, int endY) {

        }

        public void onPlayerSwipedToBench(int playerId) {
            //Member change request
        }

        public void onLeftSideBenchLongTouched(int x, int y) {

        }

        public void onRightSideBenchLongTouched(int x, int y) {

        }

        public void onPlayerTouched(int playerId, int x, int y) {

        }

        public void onPlayerLongTouched(int playerId, int x, int y) {

        }


        public void onLeftCourtInsideTouched(int x, int y) {

        }

        public void onLeftCourtOutsideTouched(int x, int y) {

        }

        public void onRightCourtInsideTouched(int x, int y) {

        }

        public void onRightCourtOutsideTouched(int x, int y) {

        }

        public void onLeftSideBackLeftSwipedToSeviceArea(int playerId, int x, int y) {

        }

        public void onRightSideBackLeftSwipeToServiceArea(int playerId, int x, int y) {

        }

        public void onScoreBoardLongTouched(int x, int y) {

        }

        public void onUndoButtonClicked() {

        }

        public void onReduButtonClicked() {

        }

    }

}
