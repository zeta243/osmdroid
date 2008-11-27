// Created by plusminus on 19:24:16 - 12.11.2008
package org.andnav.osm.views.tiles;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.andnav.osm.adt.GeoPoint;
import org.andnav.osm.util.ValuePair;
import org.andnav.osm.util.Util.PixelSetter;
import org.andnav.osm.util.constants.OSMConstants;
import org.andnav.osm.views.util.Util;
import org.andnav.osm.views.util.constants.OSMMapViewConstants;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class OSMMapTilePreloader implements OSMConstants, OSMMapViewConstants {
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
	
	/**
	 * Loads a series of MapTiles to the various caches at a specific zoomlevel.
	 */
	public void loadAllToCacheAsync(final int[][] pTiles, final int aZoomLevel, final OSMMapTileProviderInfo aRendererInfo, final OSMMapTileManager pTileManager, final OnProgressChangeListener pProgressListener){
		final int overallCount = pTiles.length;
		
		final Counter overallCounter = new Counter();
		final Counter successCounter = new Counter();
		final Handler h = new Handler(){
			@Override
			public void handleMessage(final Message msg) {
				final int what = msg.what;
				overallCounter.increment();
				switch(what){
					case OSMMapTileDownloadProvider.MAPTILEPROVIDER_SUCCESS_ID:
						successCounter.increment();
						pProgressListener.onProgressChange(successCounter.getCount(), overallCount);
						if(DEBUGMODE)
							Log.i(DEBUGTAG, "MapTile download success.");
						break;
					case OSMMapTileDownloadProvider.MAPTILEPROVIDER_FAIL_ID:
						if(DEBUGMODE)
							Log.e(DEBUGTAG, "MapTile download error.");
						break;
				}
				if(overallCounter.getCount() == overallCount 
						&& successCounter.getCount() != overallCount) // <-- Message would already have been sent!
					pProgressListener.onProgressChange(overallCount, overallCount);
					
				super.handleMessage(msg);
			}
		};
		
		for (int[] tileID : pTiles) {
			if(!pTileManager.preloadMaptileAsync(tileID, aZoomLevel, h))
				h.sendEmptyMessage(OSMMapTileDownloadProvider.MAPTILEPROVIDER_SUCCESS_ID);
		}
	}
	
	/**
	 * 
	 * @param aPath
	 * @param aZoomLevel
	 * @param aRendererInfo
	 * @param pSmoothed Smoothed by a Bresenham-Algorithm
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static int[][] getNeededMaptiles(final List<GeoPoint> aPath, final int aZoomLevel, final OSMMapTileProviderInfo aRendererInfo, final boolean pSmoothed) throws IllegalArgumentException {
		if(aZoomLevel > aRendererInfo.ZOOM_MAXLEVEL)
			throw new IllegalArgumentException("Zoomlevel higher than Renderer supplies!");
			
		final int[] reuse = new int[2];
		
		/* We need only unique MapTile-indices, so we use a Set. */
		final Set<ValuePair> needed = new TreeSet<ValuePair>(new Comparator<ValuePair>(){
			@Override
			public int compare(ValuePair a, ValuePair b) {
				return a.compareTo(b);
			}			
		});
		
		/* Contains the values of a single line. */
		final Set<ValuePair> rasterLine = new TreeSet<ValuePair>(new Comparator<ValuePair>(){
			@Override
			public int compare(ValuePair a, ValuePair b) {
				return a.compareTo(b);
			}			
		});
		
		final PixelSetter rasterLinePixelSetter = new PixelSetter(){
			@Override
			public void setPixel(final int x, final int y) {
				rasterLine.add(new ValuePair(x,y));
			}
		};
		
		GeoPoint previous = null;
		/* Get the mapTile-coords of every point in the polyline and add to the set. */
		for (GeoPoint gp : aPath) {
			Util.getMapTileFromCoordinates(gp, aZoomLevel, reuse);
			needed.add(new ValuePair(reuse));
			
			if(previous != null){
				
				
				final int prevX = reuse[0];
				final int prevY = reuse[1];
				
				Util.getMapTileFromCoordinates(GeoPoint.getGeoPointBetween(gp, previous), aZoomLevel, reuse);
				
				final int curX = reuse[0];
				final int curY = reuse[1];				
				
				rasterLine.clear();
				org.andnav.osm.util.Util.rasterLine(prevX, prevY, curX, curY, rasterLinePixelSetter);
				
				/* If wanted smooth that line. */
				if(pSmoothed){
					org.andnav.osm.util.Util.smoothLine(rasterLine);
				}
				
				needed.addAll(rasterLine);
			}
				
			previous = gp;
		}		
		
		/* Put the unique MapTile-indices into an array. */
		final int countNeeded = needed.size();
		final int[][] out = new int[countNeeded][];
		
		int i = 0;
		for (ValuePair valuePair : needed)
			out[i++] = valuePair.toArray();
		
		return out;
	}
	

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public interface OnProgressChangeListener{
		/** Betweeen 0 and 100 (including). */
		void onProgressChange(final int aProgress, final int aMax);
	}
	
	private static class Counter{
		int mCount;

		public void increment() {
			mCount++;
		}
		
		public int getCount() {
			return mCount;
		}
	}
}
