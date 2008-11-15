// Created by plusminus on 17:58:57 - 25.09.2008
package org.andnav.osm.views.tiles;

import java.util.HashMap;

import org.andnav.osm.views.util.LRUBitmapCache;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import android.graphics.Bitmap;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileMemoryCache implements OpenStreetMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected HashMap<String, Bitmap> mCachedTiles;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OSMMapTileMemoryCache(){
		this(CACHE_MAPTILECOUNT_DEFAULT);
	}
	
	/**
	 * @param aMaximumCacheSize Maximum amount of MapTiles to be hold within.
	 */
	public OSMMapTileMemoryCache(final int aMaximumCacheSize){
		this.mCachedTiles = new LRUBitmapCache(aMaximumCacheSize);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public synchronized Bitmap getMapTile(final String aTileURLString) {
		return this.mCachedTiles.get(aTileURLString);
	}

	public synchronized void putTile(final String aTileURLString, final Bitmap aTile) {
		this.mCachedTiles.put(aTileURLString, aTile);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
