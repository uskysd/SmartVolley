package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;

public class RectangleArea extends Token {
	
	private static final String TAG = RectangleArea.class.getSimpleName();
	
	private int width;
	private int height;
	private int color;
	private int strokeWidth;
	
	public RectangleArea(int x, int y, int width, int height, int color) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.color = color;
		this.strokeWidth = 10;
	}
	
	@Override
	public void update() {
		//TODO Update
	};

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(this.color);
		paint.setStrokeWidth(this.strokeWidth);
		canvas.drawRect(getX(), getY(), getX()+getWidth(), getY()+getHeight(), paint);
	};
	
	public boolean checkTouched(int x, int y) {
		if (x>getX() && x<getX()+getWidth() && y>getY() && y<getY()+getWidth()) {
			setTouched(true);
			return true; 
		} else {
			setTouched(false);
			return false;
		}
	};
	
	@Override
	public void adjustLayout(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
}
