package org.osmdroid.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MapSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private boolean mHasSurface;
	private MySurfaceViewThread mMySurfaceViewThread;
	private final MapView mMapView;

	public MapSurfaceView(Context pContext, AttributeSet attrs) {
		super(pContext, attrs);
		mMapView = (MapView) this;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHasSurface = false;
	}

	public void resume() {
		// Create and start the graphics update thread.
		if (mMySurfaceViewThread == null) {
			mMySurfaceViewThread = new MySurfaceViewThread();
			if (mHasSurface == true)
				mMySurfaceViewThread.start();
		}
	}

	public void pause() {
		// Kill the graphics update thread
		if (mMySurfaceViewThread != null) {
			mMySurfaceViewThread.requestExitAndWait();
			mMySurfaceViewThread = null;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		mHasSurface = true;
		if (mMySurfaceViewThread != null)
			mMySurfaceViewThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mHasSurface = false;
		pause();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mMySurfaceViewThread != null)
			mMySurfaceViewThread.onWindowResize(w, h);
	}

	public Surface getSurface() {
		return mHolder.getSurface();
	}

	class MySurfaceViewThread extends Thread {
		private boolean done;

		MySurfaceViewThread() {
			super();
			done = false;
		}

		@Override
		public void run() {
			SurfaceHolder surfaceHolder = mHolder;
			// Repeat the drawing loop until the thread is stopped.
			while (!done) {
				// Lock the surface and return the canvas to draw onto.
				Canvas canvas = surfaceHolder.lockCanvas();

				// Draw!!
				onDraw(canvas);

				// Unlock the canvas and render the current image.
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}

		public void requestExitAndWait() {
			// Mark this thread as complete and combine into
			// the main application thread.
			done = true;
			try {
				join();
			} catch (InterruptedException ex) {
			}
		}

		public void onWindowResize(int w, int h) {
			// Deal with a change in the available surface size.
		}
	}
}
