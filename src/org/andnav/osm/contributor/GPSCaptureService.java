package org.andnav.osm.contributor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andnav.osm.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class GPSCaptureService extends Service implements LocationListener {

	private static final String TAG = "GPSCaptureService";

	private Thread mGpsLocationProcessor;
	private boolean mRunning = false;
	private boolean mFailed = false;
	WakeLock mWakeLock;
	private List<Location> mGpsLocationQueue = Collections
			.synchronizedList(new ArrayList<Location>());
	private List<GPXFileWriter> mGpsCaptureFiles = Collections
			.synchronizedList(new ArrayList<GPXFileWriter>());
	private int mNumSatellites = 0;
	private List<GPSCaptureCallback> mGpsClientCallbacks = Collections
			.synchronizedList(new ArrayList<GPSCaptureCallback>());

	/* The public IPC interface to this service */
	private GPSCaptureInterface.Stub mInterface = new GPSCaptureInterface.Stub() {

		@Override
		public boolean newSegment(String captureName) throws RemoteException {
			try {
				synchronized (mGpsCaptureFiles) {
					for (GPXFileWriter f : mGpsCaptureFiles) {
						if (f.getName().equals(captureName)) {
							f.appendNewTrack();
						}
					}
				}
			} catch (IOException e) {
				Log.e(TAG, "Could not create new track segment: " + e.toString());
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		public boolean startCapture(final String captureName) throws RemoteException {
			GPXFileWriter f = null;
			try {
				f = new GPXFileWriter("/sdcard/" + getString(R.string.app_name), captureName);
				synchronized (mGpsCaptureFiles) {
					boolean found = false;
					// Check for duplicates
					for (GPXFileWriter fw : mGpsCaptureFiles) {
						if (fw.getName().equals(captureName)) {
							found = true;
						}
					}

					if (!found)
						mGpsCaptureFiles.add(f);
				}
			} catch (IOException e) {
				Log.e(TAG, "Could not begin capture: " + e.toString());
				e.printStackTrace();
				return false;
			}
			// Make sure the capture thread is running
			_startCapture();
			return true;
		}

		@Override
		public boolean stopCapture(String captureName) throws RemoteException {

			synchronized (mGpsCaptureFiles) {
				for (GPXFileWriter f : mGpsCaptureFiles) {
					if (f.getName().equals(captureName)) {
						try {
							f.finaliseOutputStream();
						} catch (IOException e) {
							Log.e(TAG, "Could not finalise output stream: " + e.toString());
							e.printStackTrace();
						}
						mGpsCaptureFiles.remove(f);
					}
				}
				if (mGpsCaptureFiles.isEmpty()) {
					mGpsCaptureFiles.notify();
					_stopCapture();
				}
			}

			return true;
		}

		@Override
		public boolean registerCallback(GPSCaptureCallback c) throws RemoteException {
			boolean ret;
			synchronized (mGpsClientCallbacks) {
				mGpsClientCallbacks.remove(c); // Avoid double-adds
				ret = mGpsClientCallbacks.add(c);
			}
			return ret;
		}

		@Override
		public boolean deRegisterCallback(GPSCaptureCallback c) throws RemoteException {
			boolean ret;
			synchronized (mGpsClientCallbacks) {
				ret = mGpsClientCallbacks.remove(c);
			}
			return ret;
		}

		@Override
		public int getNumSatellites() throws RemoteException {
			return GPSCaptureService.this.mNumSatellites;
		}

		@Override
		public boolean isRunning() throws RemoteException {
			return mRunning;
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Creating Service");

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "GPS Capture");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroying Service");

		_stopCapture();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mInterface;
	}

	/* LocationListener Interface */
	@Override
	public void onLocationChanged(Location location) {
		synchronized (mGpsLocationQueue) {
			mGpsLocationQueue.add(location);
			mGpsLocationQueue.notify();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// _stopCapture();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// _startCapture();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extraInfo) {
		if (status == LocationProvider.AVAILABLE) {
			mNumSatellites = extraInfo.getInt("satellites");
			// Log.d(TAG, "GPS Service is available (" +
			// extraInfo.getInt("satellites") + " satellites)");
			// _startCapture();
		} else if (status == LocationProvider.OUT_OF_SERVICE) {
			Log.d(TAG, "GPS is now OUT_OF_SERVICE");
			// _stopCapture();
		} else {
			Log.d(TAG, "GPS is now TEMPORARILY_UNAVAILABLE");
		}
	}

	/* Start the main thread which receives the location updates */
	private boolean _startCapture() {
		if (!mRunning && !mFailed) {
			mRunning = true;

			try {
				mWakeLock.acquire();
			} catch (SecurityException e) {
				Log.d(TAG, "We dont have permission to keep CPU active");
			}

			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

			mGpsLocationProcessor = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						processGPSLocationQueue();
					} catch (IOException e) {
						Log.e(TAG, "Fatal Error: " + e.toString());
						e.printStackTrace();
						mRunning = false;
						mGpsLocationProcessor.stop();
						stopSelf();
						mFailed = true;
					}
				}
			});
			mGpsLocationProcessor.setName("gpsLocationProcessor");
			mGpsLocationProcessor.start();
			return true;
		}
		return false;
	}

	/* Stop the thread which receives the Location updates */
	private void _stopCapture() {
		if (mRunning) {
			mRunning = false;

			try {
				mWakeLock.release();
			} catch (SecurityException e) {
				Log.d(TAG, "We dont have permission to keep CPU active");
			}

			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			lm.removeUpdates(this);

			synchronized (mGpsLocationQueue) {
				mGpsLocationQueue.notify();
			}

			try {
				mGpsLocationProcessor.join();
			} catch (InterruptedException e) {
				Log.e(TAG, "Could not stop thread");
				e.printStackTrace();
				mGpsLocationProcessor.stop();
			}

			mGpsLocationProcessor = null;
		}
	}

	/* The main event loop which receives location updates and processes them */
	private void processGPSLocationQueue() throws IOException {
		mRunning = true;
		// Open output file
		while (mRunning) {
			Location thisLocation = null;
			synchronized (mGpsLocationQueue) {
				if (mGpsLocationQueue.isEmpty()) {
					try {
						mGpsLocationQueue.wait();
						continue;
					} catch (InterruptedException e) {
						Log.e(TAG, "Error while waiting on gpsLocationQueue" + e.toString());
						e.printStackTrace();
					}
				} else {
					thisLocation = mGpsLocationQueue.get(0);
					mGpsLocationQueue.remove(0);
				}
			}

			if (thisLocation != null) {
				synchronized (mGpsCaptureFiles) {
					for (GPXFileWriter f : mGpsCaptureFiles) {
						try {
							f.appendLocation(thisLocation);
						} catch (IOException e) {
							mGpsCaptureFiles.remove(f);
							f.finaliseOutputStream();
						}
					}
				}

				synchronized (mGpsClientCallbacks) {
					for (GPSCaptureCallback c : mGpsClientCallbacks) {
						try {
							c.updateLocation(thisLocation);
						} catch (RemoteException e) {
							mGpsClientCallbacks.remove(c);
						}
					}
				}
			}
		}
	}
}
