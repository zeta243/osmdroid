package org.andnav.osm.contributor.android;

import org.andnav.osm.contributor.util.RecordedGeoPoint;

import android.location.Location;

/**
 * 
 * @author Neil Boyd
 *
 */
public class RouteRecorder extends org.andnav.osm.contributor.RouteRecorder {

	public void add(final Location aLocation, final int aNumSatellites){
		this.mRecords.add(new RecordedGeoPoint(
					(int)(aLocation.getLatitude() * 1E6), 
					(int)(aLocation.getLongitude() * 1E6),
					System.currentTimeMillis(),
					aNumSatellites));
	}
	
}
