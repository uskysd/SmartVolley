package uskysd.smartvolley.graphics;

import android.graphics.Canvas;

public abstract class AdjustableDrawable {
	
	public abstract void update();
	
	public abstract void draw(Canvas canvas);
	
	public abstract void adjustLayout(int width, int height);


}
