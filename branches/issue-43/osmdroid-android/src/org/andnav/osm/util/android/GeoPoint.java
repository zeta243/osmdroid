package org.andnav.osm.util.android;

import android.location.Location;

/**
 * 
 * @author Neil Boyd
 *
 */
public class GeoPoint extends org.andnav.osm.util.GeoPoint {
	
	public GeoPoint(final Location pLocation) {
		super(pLocation.getLatitude(), pLocation.getLongitude());
	}

}
