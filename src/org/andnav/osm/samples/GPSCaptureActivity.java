package org.andnav.osm.samples;

import java.util.Calendar;
import java.util.List;

import org.andnav.osm.OpenStreetMapActivity;
import org.andnav.osm.R;
import org.andnav.osm.contributor.GPSCaptureCallback;
import org.andnav.osm.contributor.GPSCaptureInterface;
import org.andnav.osm.util.TypeConverter;
import org.andnav.osm.util.constants.OpenStreetMapConstants;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.controller.OpenStreetMapViewController;
import org.andnav.osm.views.overlay.OpenStreetMapViewLinearOverlay;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;
import org.andnav.osm.views.overlay.OpenStreetMapViewSimpleLocationOverlay;
import org.andnav.osm.views.util.OpenStreetMapRendererInfo;

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
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;



public class GPSCaptureActivity extends OpenStreetMapActivity implements OpenStreetMapConstants {
    /** Called when the activity is first created. */
	
	private static final int MENU_ZOOMIN_ID = Menu.FIRST;
	private static final int MENU_ZOOMOUT_ID = MENU_ZOOMIN_ID + 1;
	private static final int MENU_RENDERER_ID = MENU_ZOOMOUT_ID + 1;
	private static final int MENU_ANIMATION_ID = MENU_RENDERER_ID + 1;
	private static final int MENU_MINIMAP_ID = MENU_ANIMATION_ID + 1;
	
	private static final String TAG = "GPSCaptureActivity";
	
	private GPSCaptureInterface mCaptureService = null;
	
	private boolean  mBound = false;
	private boolean  mStarted = false;
	private boolean  mStartOnConnect = false;
	private Button   mStartStopButton;
	private Button   mNewSegmentButton;
	/*
	private TextView mCurrentLocationValue;
	private TextView mCurrentSpeedValue;
	private TextView mCurrentBearingValue;
	private TextView mNumSatellitesValue;
	private TextView mAccuracyValue;
	*/
	private EditText mTraceNameEditor;
	
	private Location mLastLocation;
	private Location mLatestLocation;
	
	private RelativeLayout mOSMLayout;
	private OpenStreetMapView mOSMView;
	private OpenStreetMapViewSimpleLocationOverlay mMyLocationOverlay;
	// private OpenStreetMapView mOsmvMinimap; 
	
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
            mStartStopButton.setEnabled(true);
            if (mStartOnConnect)
            {
            	mStarted = false;
            	startStopTrace(mStartStopButton);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
        	GPSCaptureActivity.this.mCaptureService = null;
        }
    };
    
	private static final int UPDATE_LOCATION = 1;
    GPSCaptureCallback mCallback = new GPSCaptureCallback.Stub() {

		@Override
		public void updateLocation(Location location) throws RemoteException {
			mHandler.sendMessage(mHandler.obtainMessage(UPDATE_LOCATION, 0));
			
			mLastLocation = mLatestLocation;
			mLatestLocation = location;
			
		}
    };
    
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LOCATION:
                	if (mLatestLocation != null)
                	{
                		/*
                		String latlong = String.format("%3.5f, %3.5f", mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        mCurrentLocationValue.setText(latlong);
                        mCurrentSpeedValue.setText(mLastLocation.getSpeed() + "");
                        mCurrentBearingValue.setText(mLastLocation.getBearing() + "");
                        
                        try {
							mNumSatellitesValue.setText(mCaptureService.getNumSatellites() + "");
						} catch (RemoteException e) {
							Log.e(TAG, "Could not get number of Satellites");
							e.printStackTrace();
						}
                        mAccuracyValue.setText(mLastLocation.getAccuracy() + "");
                        */
                		
                        mMyLocationOverlay.setLocation(TypeConverter.locationToGeoPoint(mLatestLocation));
                        mOSMView.invalidate();
                        mOSMView.setMapCenter(mLatestLocation.getLatitude(), mLatestLocation.getLongitude());
                        
                        if (mLastLocation != null && mLatestLocation != null)
                        {              
                            List <OpenStreetMapViewOverlay> overlays = mOSMView.getOverlays();
                            
                	        overlays.add(new OpenStreetMapViewLinearOverlay(GPSCaptureActivity.this,
                	        		TypeConverter.locationToGeoPoint(mLastLocation),
                	        		TypeConverter.locationToGeoPoint(mLatestLocation)));
                	        
                	        // Trim them a bit - we dont want too many
                	        while (overlays.size() > 50)
                	        {
                	        	overlays.remove(1);
                	        }
                	        
                        }
                    	// mLastLocation;
                    	// mLatestLocation;
                	}
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    
    @Override

    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate() called");
    	
        super.onCreate(savedInstanceState, false);
        
        setContentView(R.layout.main);

//        try {
//        	Intent svc = new Intent(this, GPSCaptureService.class);
//        	Log.d(TAG, "Starting Service");
//        	startService(svc);
//        }
//        catch (Exception e)
//        {
//        	Log.d(TAG, "Service Problem", e);
//        }
        
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
        
        mStartStopButton = (Button)findViewById(R.id.StartStopButton);
        mStartStopButton.setEnabled(false);
        mStartStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startStopTrace((Button)v);
			}
        });
        
        mNewSegmentButton = (Button)findViewById(R.id.NewSegmentButton);
        mNewSegmentButton.setEnabled(false);
        mNewSegmentButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
            	try {
        			mCaptureService.newSegment(mTraceNameEditor.getText().toString());
        		} catch (RemoteException e) {
        			Log.e(TAG, "Error starting capture: " + e.toString());
        			e.printStackTrace();
        		}
        	}
        });

        /*
    	mCurrentLocationValue = (TextView)findViewById(R.id.CurrentLocationValue);
    	mCurrentSpeedValue    = (TextView)findViewById(R.id.CurrentSpeedValue);
    	mCurrentBearingValue  = (TextView)findViewById(R.id.CurrentBearingValue);
    	
    	mNumSatellitesValue = (TextView)findViewById(R.id.NumSatellitesValue);
    	mAccuracyValue      = (TextView)findViewById(R.id.AccuracyValue);
    	*/
        
    	mTraceNameEditor = (EditText)findViewById(R.id.traceNameEditor);
    	mTraceNameEditor.selectAll();
    	
    	mOSMLayout = new RelativeLayout(this);
    	// mOSMLayout = (RelativeLayout)findViewById(R.id.MapLayout);

    	mOSMView = new OpenStreetMapView(this, OpenStreetMapRendererInfo.MAPNIK);
    	
    	mOSMView.setZoomLevel(15);

        mOSMLayout.addView(this.mOSMView, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        
        ll.addView(mOSMLayout);
        
        /* SingleLocation-Overlay */
        {
	        /* Create a static Overlay showing a single location. (Gets updated in onLocationChanged(Location loc)! */
	        mMyLocationOverlay = new OpenStreetMapViewSimpleLocationOverlay(this);
	        mOSMView.getOverlays().add(mMyLocationOverlay);
        }
        
        /* ZoomControls */
        {
	        /* Create a ImageView with a zoomIn-Icon. */
	        final ImageView ivZoomIn = new ImageView(this);
	        ivZoomIn.setImageResource(R.drawable.zoom_in);
	        /* Create RelativeLayoutParams, that position in in the top right corner. */
	        final RelativeLayout.LayoutParams zoominParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        zoominParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
	        zoominParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	        mOSMLayout.addView(ivZoomIn, zoominParams);
	        
	        ivZoomIn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					mOSMView.zoomIn();
				}
	        });
	        
	        
	        /* Create a ImageView with a zoomOut-Icon. */
	        final ImageView ivZoomOut = new ImageView(this);
	        ivZoomOut.setImageResource(R.drawable.zoom_out);
	        
	        /* Create RelativeLayoutParams, that position in in the top left corner. */
	        final RelativeLayout.LayoutParams zoomoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        zoomoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
	        zoomoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	        mOSMLayout.addView(ivZoomOut, zoomoutParams);
	        
	        ivZoomOut.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					mOSMView.zoomOut();
				}
	        });
        }
        
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null)
        {
            mOSMView.setMapCenter(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        
//        /* MiniMap */
//        {
//	        /* Create another OpenStreetMapView, that will act as the MiniMap for the 'MainMap'. They will share the TileProvider. */
//	        mOsmvMinimap = new OpenStreetMapView(this, OpenStreetMapRendererInfo.CLOUDMADESTANDARDTILES, mOSMView);
//	        final int aZoomDiff = 3; // Use OpenStreetMapViewConstants.NOT_SET to disable autozooming of this minimap
//	        mOSMView.setMiniMap(mOsmvMinimap, aZoomDiff);
//	        
//	        
//	        /* Create RelativeLayout.LayoutParams that position the MiniMap on the top-right corner of the RelativeLayout. */
//	        RelativeLayout.LayoutParams minimapParams = new RelativeLayout.LayoutParams(90, 90);
//	        minimapParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//	        minimapParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//	        minimapParams.setMargins(5,5,5,5);
//	        mOSMLayout.addView(mOsmvMinimap, minimapParams);
//        }
    }
    
    private void startStopTrace(Button b)
    {
    	Log.d(TAG, "startStopTrace(Button) called");
    	if (mStarted)
    	{
    		// Stop Capture
    		try {
				mCaptureService.stopCapture(mTraceNameEditor.getText().toString());
			} catch (RemoteException e) {
				Log.e(TAG, "Error stopping GPS Capture");
				e.printStackTrace();
			}
			mStarted = false;
			mStartStopButton.setText(R.string.start_gps_trace_btn);
			mNewSegmentButton.setEnabled(false);
			mTraceNameEditor.setEnabled(true);
			mTraceNameEditor.setText("(Trace Name)");
			mTraceNameEditor.selectAll();
			
			Toast t = Toast.makeText(this, R.string.gps_capture_completed, Toast.LENGTH_SHORT);
			t.show();
    	}
    	else
    	{
    		// Start Capture
    		try {
    			// Remove all overlays (except overlay 0 - which is the "current location")
    			List <OpenStreetMapViewOverlay> overlays = mOSMView.getOverlays();
    	        while (overlays.size() > 1)
    	        {
    	        	overlays.remove(1);
    	        }
    	        
    			String captureName = mTraceNameEditor.getText().toString();
    			if (captureName.equals("(Trace Name)"))
    			{
    				Calendar c = Calendar.getInstance();
    			    String fileName = String.format("%04d-%02d-%02d %02d-%02d.%02d",
    				    	c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
    					    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
				    mCaptureService.startCapture(fileName);
				    mTraceNameEditor.setText(fileName);
    			}
    			else
    			{
    				mCaptureService.startCapture(captureName);
    			}
			} catch (RemoteException e) {
				Log.e(TAG, "Error starting capture: " + e.toString());
				e.printStackTrace();
			}
			mStarted = true;
			mStartStopButton.setText(R.string.stop_gps_trace_btn);
			mNewSegmentButton.setEnabled(true);
			mTraceNameEditor.setEnabled(false);
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
    
    @Override
	public boolean onCreateOptionsMenu(final Menu pMenu) {
    	pMenu.add(0, MENU_ZOOMIN_ID, Menu.NONE, "ZoomIn");
    	pMenu.add(0, MENU_ZOOMOUT_ID, Menu.NONE, "ZoomOut");
    	
    	final SubMenu subMenu = pMenu.addSubMenu(0, MENU_RENDERER_ID, Menu.NONE, "Choose Renderer");
    	{
	    	for(int i = 0; i < OpenStreetMapRendererInfo.values().length; i ++)
	    		subMenu.add(0, 1000 + i, Menu.NONE, OpenStreetMapRendererInfo.values()[i].NAME);
    	}
    	
    	pMenu.add(0, MENU_ANIMATION_ID, Menu.NONE, "Run Animation");
    	pMenu.add(0, MENU_MINIMAP_ID, Menu.NONE, "Toggle Minimap");
    	
    	return true;
	}
    
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case MENU_ZOOMIN_ID:
				this.mOSMView.zoomIn();
				return true;
				
			case MENU_ZOOMOUT_ID:
				this.mOSMView.zoomOut();
				return true;
				
			case MENU_RENDERER_ID:
				this.mOSMView.invalidate();
				return true;
				
			case MENU_MINIMAP_ID:
				switch(this.mOSMView.getOverrideMiniMapVisiblity()){
					case View.VISIBLE:
						this.mOSMView.setOverrideMiniMapVisiblity(View.INVISIBLE);
						break;
					case OpenStreetMapConstants.NOT_SET:
					case View.INVISIBLE:
					case View.GONE:
						this.mOSMView.setOverrideMiniMapVisiblity(View.VISIBLE);
						break;
				}					
				return true;
				
			case MENU_ANIMATION_ID:
				this.mOSMView.getController().animateTo(52370816, 9735936, OpenStreetMapViewController.AnimationType.MIDDLEPEAKSPEED, OpenStreetMapViewController.ANIMATION_SMOOTHNESS_HIGH, OpenStreetMapViewController.ANIMATION_DURATION_DEFAULT); // Hannover
				// Stop the Animation after 500ms  (just to show that it works)
//				new Handler().postDelayed(new Runnable(){
//					@Override
//					public void run() {
//						SampleExtensive.this.mOsmv.getController().stopAnimation(false);
//					}
//				}, 500);
				return true;
				
			default: 
				this.mOSMView.setRenderer(OpenStreetMapRendererInfo.values()[item.getItemId() - 1000]);
		}
		return false;
	}

	@Override
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationLost() {
		// TODO Auto-generated method stub
		
	}
    
}