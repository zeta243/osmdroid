// Created by plusminus on 12:28:16 - 21.09.2008
package org.andnav.osm.contributor.util;

import java.util.ArrayList;

import org.andnav.osm.adt.GPSGeoLocation;
import org.andnav.osm.adt.GeoPoint;
import org.andnav.osm.adt.util.TypeConverter;

import android.location.Location;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class RouteRecorder {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected final ArrayList<GPSGeoLocation> mRecords = new ArrayList<GPSGeoLocation>();

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public ArrayList<GPSGeoLocation> getRecordedGeoPoints() {
		return this.mRecords;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void add(final Location aLocation){
		this.mRecords.add(TypeConverter.locationToGPSGeoLocation(aLocation));
	}
	
	public void add(final GeoPoint aGeoPoint){
		this.mRecords.add(new GPSGeoLocation(
					aGeoPoint.getLatitudeE6(), 
					aGeoPoint.getLongitudeE6(),
					System.currentTimeMillis()));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
