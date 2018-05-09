package uskysd.smartvolley.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class RectangleArea extends Token {
	
	private static final String TAG = RectangleArea.class.getSimpleName();
	
	private int width;
	private int height;
	private int color;
	private int strokeWidth;
	private String text;
	private int textSize;
	private int textColor;
	
	public RectangleArea(int x, int y, int width, int height, int color) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.color = color;
		this.strokeWidth = 10;
		this.text = null;
		this.textSize = (int) (0.8*height);
		this.textColor = Color.BLACK;
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

		if (text != null) {
			Paint paint2 = new Paint();
			paint2.setColor(textColor);
			paint2.setTextSize(textSize);
			paint2.setTextAlign(Paint.Align.CENTER);

			// To get text height
			Rect r = new Rect();
			paint2.getTextBounds(text, 0, text.length(), r);

			// Draw text
			canvas.drawText(text, getCenterX(), getCenterY()+(int)(0.5*r.height()), paint2);

		}




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

	public int getCenterX() {
		return getX() + (int) (0.5*getWidth());
	}

	public int getCenterY() {
		return getY() + (int) (0.5*getHeight());
	}

	public int getBottomY() {
	    return getY() + getHeight();
    }

    public int getRightX() {
	    return getX() + getWidth();
    }

    public int getTextSize() {
		return textSize;
	}

	public String getText() {
		return text;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTextSize(int size) {
		this.textSize = size;
	}

	public void setTextColor(int color) {
		this.textColor = color;
	}
	
}
