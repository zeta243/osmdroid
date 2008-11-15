package org.andnav.osm.samples;

import java.util.Calendar;
import java.util.List;

import org.andnav.osm.R;
import org.andnav.osm.adt.util.TypeConverter;
import org.andnav.osm.contributor.service.GPSCaptureCallback;
import org.andnav.osm.contributor.service.GPSCaptureInterface;
import org.andnav.osm.util.constants.OSMConstants;
import org.andnav.osm.views.OSMMapView;
import org.andnav.osm.views.controller.OSMMapViewController;
import org.andnav.osm.views.overlay.OSMMapViewLinearOverlay;
import org.andnav.osm.views.overlay.OSMMapViewOverlay;
import org.andnav.osm.views.overlay.OSMMapViewSimpleLocationOverlay;
import org.andnav.osm.views.tiles.OSMMapTileProviderInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;



public class GPSCaptureActivitySimple extends Activity implements OSMConstants {
    /** Called when the activity is first created. */

	private static final String TAG = "GPSCaptureActivitySimple";
	
	private GPSCaptureInterface mCaptureService = null;
	
	private boolean  mBound = false;
	private boolean  mStarted = false;
	private boolean  mStartOnConnect = true;
	
	private Location mLatestLocation;
	
	private TextView mCurrentLocationValue;
	private TextView mCurrentSpeedValue;
	private TextView mCurrentBearingValue;
	private TextView mNumSatellitesValue;
	private TextView mAccuracyValue;
	
	private String captureName;
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
        	
        	Log.d(TAG, "Creating Service Connection");
            mCaptureService = GPSCaptureInterface.Stub.asInterface(service);
            try {
				mCaptureService.registerCallback(mCallback);
			} catch (RemoteException e) {
				Log.e(TAG, "Could not register call-back function");
				e.printStackTrace();
			}
			
            if (mStartOnConnect)
            {
            	mStarted = false;
            	startStopTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        	GPSCaptureActivitySimple.this.mCaptureService = null;
        }
    };
    
	private static final int UPDATE_LOCATION = 1;
    GPSCaptureCallback mCallback = new GPSCaptureCallback.Stub() {

		@Override
		public void updateLocation(Location location) throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(UPDATE_LOCATION, 0));
			mLatestLocation = location;
			
		}
    };
    
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LOCATION:
                	if (mLatestLocation != null)
                	{                		
                		String latlong = String.format("%3.5f, %3.5f", mLatestLocation.getLatitude(), mLatestLocation.getLongitude());
                        mCurrentLocationValue.setText(latlong);
                        mCurrentSpeedValue.setText(mLatestLocation.getSpeed() + "");
                        mCurrentBearingValue.setText(mLatestLocation.getBearing() + "");
                        
                        try {
							mNumSatellitesValue.setText(mCaptureService.getNumSatellites() + "");
						} catch (RemoteException e) {
							Log.e(TAG, "Could not get number of Satellites");
							e.printStackTrace();
						}
                        mAccuracyValue.setText(mLatestLocation.getAccuracy() + "");
                	}
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    
    @Override

    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gps_capture_simple_layout);
      
    	if (savedInstanceState != null)
    	{
    	    if (savedInstanceState.getBoolean("captureStarted"))
    	    {
    	        mStartOnConnect = true;
    	    }
    	}

        Log.d(TAG, "Binding to service");
        if (bindService(new Intent(GPSCaptureInterface.class.getName()),
           mConnection, Context.BIND_AUTO_CREATE))
        {
    	    Log.d(TAG, "Binding OK");
    	    mBound = true;
        }	
        else
        {
    	    Log.d(TAG, "Binding Failed");
        }
        LinearLayout ll = (LinearLayout)findViewById(R.id.MainView);
    }
    
    private void startStopTrace()
    {
    	Log.d(TAG, "startStopTrace(Button) called");
    	if (mStarted)
    	{
    		// Stop Capture
    		try {
				mCaptureService.stopCapture(captureName);
			} catch (RemoteException e) {
				Log.e(TAG, "Error stopping GPS Capture");
				e.printStackTrace();
			}
			mStarted = false;

			Toast t = Toast.makeText(this, R.string.gps_capture_completed, Toast.LENGTH_SHORT);
			t.show();
    	}
    	else
    	{
    		// Start Capture
    		try {
    				Calendar c = Calendar.getInstance();
    			    captureName = String.format("%04d-%02d-%02d %02d-%02d.%02d",
    				    	c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
    					    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
				    mCaptureService.startCapture(captureName);
			} catch (RemoteException e) {
				Log.e(TAG, "Error starting capture: " + e.toString());
				e.printStackTrace();
			}
			mStarted = true;
    	}
    }
    
    @Override
    public void onDestroy()
    {
    	Log.d(TAG, "onDestroy() called");
        super.onDestroy();
        // Dont stop the service
        //Intent svc = new Intent(this, GPSCaptureService.class);
        //Log.d(TAG, "Stopping Activity");
        //stopService(svc);
        
        if (mBound)
        {
            unbindService(mConnection);
            mBound = false;
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
    	Log.d(TAG, "onSaveInstanceState() called");
    	super.onSaveInstanceState(outState);
        outState.putBoolean("captureStarted", mStarted);
    }
    
    @Override
    public void onStop()
    {
    	Log.d(TAG, "onStop() called");
    	super.onStop();
    }
}