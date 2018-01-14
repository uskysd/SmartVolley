package uskysd.smartvolley.graphics;

import android.graphics.Canvas;

public abstract class Token extends AdjustableDrawable {
	
	private int x;
	private int y;
	private boolean touched;
    private boolean touchable;
	
	public Token(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public abstract void update();
	
	public abstract void draw(Canvas canvas);
	
	public abstract void adjustLayout(int width, int height);

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

    public void setLocation(int x, int y) {
        setX(x);
        setY(y);
    }

	public boolean isTouched() {
		return touched;
	}

	public void setTouched(boolean touched) {
        if (isTouchable()) {
            this.touched = touched;
        }
    }

    public boolean isTouchable() {
        return this.touchable;
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
        }



	
	

}
