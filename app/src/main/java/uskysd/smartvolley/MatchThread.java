package uskysd.smartvolley;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.text.DecimalFormat;

public class MatchThread extends Thread {
	
	private static final String TAG = MatchThread.class.getSimpleName();
	
	private boolean running;
	private MatchView matchView;
	private SurfaceHolder surfaceHolder;
	private Canvas canvas;
	
	//desired fps
	private final static int MAX_FPS = 50;
	//maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	//the frame period
	private final static int FRAME_PERIOD = 1000/MAX_FPS;
		
	//Stuff for status
	private DecimalFormat df = new DecimalFormat("0.##");
	private final static int STAT_INTERVAL = 1000;//msec
	private final static int FPS_HISTORY_NR = 10;
	private long lastStatusStore = 0;
	private long statusIntervalTimer = 0l;
	private long totalFramesSkipped = 0l;
	private long framesSkippedPerStatCycle = 0l;
	
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	private double fpsStore[];
	private long statsCount = 0;
	private double averageFps = 0.0;
	
	public MatchThread(SurfaceHolder surfaceHolder, MatchView matchView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.matchView = matchView;
	}
	
	public void run() {
		
		initTimingElements();
		
		long tickCount = 0l;
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;
		
		sleepTime = 0;
		
		while (running) {
			tickCount++;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;
					
					//update game state
					this.matchView.update();
					
					//Draw canvas on the match view
					this.matchView.render(canvas);
					
					timeDiff = System.currentTimeMillis() - beginTime;
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					if (sleepTime >= 0) {
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {}
					}
					
					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						//Skip a frame to catch up
						this.matchView.update();
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
					if (framesSkipped > 0) {
						Log.d(TAG, "Skipped: " + framesSkipped);
					}
					
					//for statistics
					framesSkippedPerStatCycle += framesSkipped;
					storeStats();
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
		Log.d(TAG, "View loop executed "+ tickCount + " times");
		
	}
	
	public void storeStats() {
		frameCountPerStatCycle++;
		totalFrameCount++;
		
		//check the actual time
		statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);
		
		if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL) {
			double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL/1000));
			
			//store the latest fps 
			fpsStore[(int)(statsCount % FPS_HISTORY_NR)] = actualFps;
			
			statsCount++;
			
			//calculate average fps
			double totalFps = 0.0;
			for (double fps: fpsStore) {
				totalFps += fps;
			}
			if (statsCount <= FPS_HISTORY_NR) {
				averageFps = totalFps / statsCount;
			} else {
				averageFps = totalFps / FPS_HISTORY_NR;
			}
			
			totalFramesSkipped += framesSkippedPerStatCycle;
			
			//resetting the counters
			framesSkippedPerStatCycle = 0;
			frameCountPerStatCycle = 0;
			
			statusIntervalTimer = System.currentTimeMillis();
			lastStatusStore = statusIntervalTimer;
			
			Log.d(TAG, "Avarage FPS: " + df.format(averageFps));
			matchView.setAvgFps(averageFps);
			
		}
		
	}
	
	public void initTimingElements() {
		fpsStore = new double [FPS_HISTORY_NR];
		for (double fps: fpsStore) {
			fps = 0.0;
		}
		Log.d(TAG+".initTimeElements()", "Timing elements for stats initialized");
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	
	
}
