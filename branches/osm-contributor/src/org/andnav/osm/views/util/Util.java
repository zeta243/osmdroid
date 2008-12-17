// Created by plusminus on 17:53:07 - 25.09.2008
package org.andnav.osm.views.util;

import org.andnav.osm.adt.BoundingBoxE6;
import org.andnav.osm.adt.GeoPoint;
import org.andnav.osm.views.tiles.renderer.mapnik.MapnikEnvelope;
import org.andnav.osm.views.util.constants.OSMMapViewConstants;

import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class Util implements OSMMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	public static int[] getMapTileFromCoordinates(final GeoPoint gp, final int zoom, final int[] reuse) {
		return getMapTileFromCoordinates(gp.getLatitudeE6() / 1E6, gp.getLongitudeE6() / 1E6, zoom, reuse);
	}
	
	public static int[] getMapTileFromCoordinates(final int aLat, final int aLon, final int zoom, final int[] reuse) {
		return getMapTileFromCoordinates(aLat / 1E6, aLon / 1E6, zoom, reuse);
	}
	
	public static int[] getMapTileFromCoordinates(final double aLat, final double aLon, final int zoom, final int[] aUseAsReturnValue) {
		final int[] out = (aUseAsReturnValue != null) ? aUseAsReturnValue : new int[2];

		out[MAPTILE_LATITUDE_INDEX] = (int) Math.floor((1 - Math.log(Math.tan(aLat * Math.PI / 180) + 1 / Math.cos(aLat * Math.PI / 180)) / Math.PI) / 2 * (1 << zoom));
		out[MAPTILE_LONGITUDE_INDEX] = (int) Math.floor((aLon + 180) / 360 * (1 << zoom));

		return out;
	}
	
	// Conversion of a MapTile to a BoudingBox
	
	public static BoundingBoxE6 getBoundingBoxFromMapTile(final int[] aMapTile, final int zoom) {
		final int y = aMapTile[MAPTILE_LATITUDE_INDEX];
		final int x = aMapTile[MAPTILE_LONGITUDE_INDEX];
		return new BoundingBoxE6(tile2lat(y, zoom), tile2lon(x + 1, zoom), tile2lat(y + 1, zoom), tile2lon(x, zoom));
	}
	
	public static MapnikEnvelope getMapnikEnvelopeFromMapTile(final int[] aMapTile, final int zoom)
	{
		final int y = aMapTile[MAPTILE_LATITUDE_INDEX];
		final int x = aMapTile[MAPTILE_LONGITUDE_INDEX];
		
		double ll_min_x = tile2lon(x, zoom);
		double ll_max_x = tile2lon(x + 1, zoom);
		double ll_min_y = tile2lat(y + 1, zoom);
		double ll_max_y = tile2lat(y, zoom);
		
		Log.d("GetEnvelopeFromMapTile", "LL BBox for MapTile: (" + zoom + "," + x + "," + y + ") - " + ll_min_x + "," + ll_max_y + " - " + ll_max_x + "," + ll_min_y);

		double mp_min_x   = MercatorSpherical.lon2x(ll_min_x);
		double mp_max_x   = MercatorSpherical.lon2x(ll_max_x);
		double mp_min_y   = MercatorSpherical.lat2y(ll_min_y);
		double mp_max_y   = MercatorSpherical.lat2y(ll_max_y);
		
		//double[] min_xy   = MercatorElliptical.merc(ll_min_x, ll_min_y);
		//double[] max_xy   = MercatorElliptical.merc(ll_max_x, ll_max_y);
		
		Log.d("GetEnvelopeFromMapTile", "MP BBox for MapTile: (" + zoom + "," + x + "," + y + ") - " + mp_min_x + "," + mp_max_y + " - " + mp_max_x + "," + mp_min_y);
		
		return new MapnikEnvelope(mp_min_x,
								  mp_min_y,
								  mp_max_x,
								  mp_max_y
				                  );
	}
	
	private static double tile2lon(int x, int aZoom) {
		return (x / Math.pow(2.0, aZoom) * 360.0) - 180;
	}

	private static double tile2lat(int y, int aZoom) {
		final double n = Math.PI - ((2.0 * Math.PI * y) / Math.pow(2.0, aZoom));
		return 180.0 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
