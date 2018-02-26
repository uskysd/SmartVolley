package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import uskysd.smartvolley.Position;


public class PlayerToken extends Token {
	
    private int playerId = -1;
	private int number;

	//private int bodyColor = Color.parseColor(String.valueOf(R.color.player_token_normal));
	private int bodyColor = Color.GREEN;
	//private int numberColor = Color.parseColor(String.valueOf(R.color.player_token_text));
	private int numberColor = Color.BLACK;
    //private int markerColor = Color.parseColor(String.valueOf(R.color.player_marker));
	private int markerColor = Color.CYAN;
	private static int radius = 20;
	private static int textSize = 20;
	private Position position;
	private Court.Side side;

	
	public PlayerToken(int x, int y) {
		super(x, y);
	}
	
	public PlayerToken(int x, int y, int number, int playerId) {
		super(x, y);
		this.number = number;
        this.playerId = playerId;

	}
	
	public PlayerToken(Court.Side side, Position position) {
		super(0, 0);
		this.setSide(side);
		this.setPosition(position);
	}
	@Override
	public void setX(int x) {
		int centerX = Court.getCenterX();
		int courtWidth = Court.getCourtWidth();
		switch (side) {
		case LEFT_COURT:
			if (x < radius) {
				super.setX(radius);
			} else if (x < centerX-radius) {
				super.setX(x);
			} else {
				//Cannot cross the center line
				super.setX(centerX-radius);
			}
			break;
		case RIGHT_COURT:
			if (x > courtWidth-radius) {
				super.setX(courtWidth-radius);
			} else if (x > centerX+radius) {
				super.setX(x);
			} else {
				super.setX(centerX+radius);
			}
			break;
		}
	}
	
	@Override
	public void setY(int y) {
		int courtHeight = Court.getCourtHeight();
		if (y < radius) {
			super.setY(radius);
		} else if (y > courtHeight-radius) {
			super.setY(courtHeight-radius);
		} else {
			super.setY(y);
		}
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint paint = new Paint();
		paint.setColor(this.bodyColor);
		canvas.drawCircle(this.getX(), this.getY(), getRadius(), paint);
		
		//draw number
		paint.setColor(this.numberColor);
		paint.setTextSize(getTextSize());
		paint.setTextAlign(Paint.Align.CENTER);
		
		canvas.drawText(String.valueOf(this.number), this.getX(), this.getY(), paint);

	}
	
	@Override
	public void adjustLayout(int width, int height) {
		// TODO adjust token radius and text size
        PlayerToken.setRadius((int) 0.2 * height);
        PlayerToken.setTextSize((int) 0.2 * height);

		
	}
	
	public boolean checkTouched(int x, int y) {
		int squareDistance = (x-this.getX())*(x-this.getX())+(y-this.getY())*(y-this.getY());
		if (squareDistance < radius * radius) {
			setTouched(true);
			return true;
		} else {
			setTouched(false);
			return false;
		}
	}

    public PlayerToken getMarker() {
        PlayerToken marker = new PlayerToken(getX(), getY(), getNumber(), getPlayerId());
        marker.setTouchable(false);
        marker.setBodyColor(markerColor);
        return marker;
    }

    public int getPlayerId() {return playerId;}

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getBodyColor() {
		return bodyColor;
	}

	public void setBodyColor(int bodyColor) {
		this.bodyColor = bodyColor;
	}

	public int getNumberColor() {
		return numberColor;
	}

	public void setNumberColor(int numberColor) {
		this.numberColor = numberColor;
	}


	public static int getRadius() {
		return radius;
	}

	public static void setRadius(int newRadius) {
		radius = newRadius;
	}

	public static int getTextSize() {
		return textSize;
	}

	public static void setTextSize(int newTextSize) {
		textSize = newTextSize;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Court.Side getSide() {
		return side;
	}

	public void setSide(Court.Side side) {
		this.side = side;
	}

	
	
}
