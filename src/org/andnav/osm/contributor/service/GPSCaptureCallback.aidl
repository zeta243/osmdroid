
package org.andnav.osm.contributor.service;


import android.os.IBinder;


oneway interface GPSCaptureCallback
{
	void updateLocation(in Location l);
}