// Created by plusminus on 21:46:22 - 25.09.2008
package org.andnav.osm.views.tiles;

import org.andnav.osm.R;
import org.andnav.osm.util.constants.OSMConstants;
import org.andnav.osm.views.util.constants.OSMMapViewConstants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileManager implements OSMConstants, OSMMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected final Bitmap mLoadingMapTile;
	protected Context mCtx;
	protected OSMMapTileMemoryCache mMemoryTileCache;
	protected OSMMapTileFilesystemCache mFSTileCache;
	protected OSMAbstractMapTileProvider mTileProvider;
	private Handler mLoadCallbackHandler = new LoadCallbackHandler();
	private Handler mDownloadFinishedListenerHander;
	private OSMMapTileProviderInfo mProviderInfo;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OSMMapTileManager(final Context ctx, final OSMMapTileProviderInfo pProviderInfo, final Handler aDownloadFinishedListener) {
		this.mCtx = ctx;
		this.mLoadingMapTile = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.maptile_loading);
		this.mMemoryTileCache = new OSMMapTileMemoryCache();
		this.mFSTileCache = new OSMMapTileFilesystemCache(ctx, 4 * 1024 * 1024, this.mMemoryTileCache); // 4MB FSCache
		
		switch (pProviderInfo.PROVIDER_TYPE) {
		    case LOCAL_PROVIDER:
		    	this.mTileProvider = new OSMMapTileRenderProvider(ctx, pProviderInfo, this.mFSTileCache);
		    	break;
		    case DOWNLOAD_PROVIDER:
		    default:
			    this.mTileProvider = new OSMMapTileDownloadProvider(ctx, pProviderInfo, this.mFSTileCache);
			    break;
		}
		
		this.mDownloadFinishedListenerHander = aDownloadFinishedListener;
		this.mProviderInfo = pProviderInfo;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public void setProvider(OSMMapTileProviderInfo aProviderInfo){
		this.mProviderInfo = aProviderInfo;
		this.mTileProvider.setProviderInfo(aProviderInfo);
	}
	
	public OSMMapTileFilesystemCache getFileSystemCache(){
		return this.mFSTileCache;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	public void preCacheTile(int[] coords, int zoomLevel) {
		getMapTile(coords, zoomLevel);
	}
	
	/**
	 * @return <code>false</code>, when MapTile is already pending for download or already existing on the FS. <code>false</code> otherwise. 
	 */
	public boolean preloadMaptileAsync(final int[] coords, final int zoomLevel, final Handler h){
		if(this.mFSTileCache.exists(this.mProviderInfo.getTileURLString(coords, zoomLevel)))
			return false;
		
		return this.mTileProvider.requestMapTileAsync(coords, zoomLevel, h);
	}

	public Bitmap getMapTile(int[] coords, int zoomLevel)
	{
		final String aTileURLString = this.mProviderInfo.getTileURLString(coords, zoomLevel);
		
		Bitmap ret = this.mMemoryTileCache.getMapTile(aTileURLString);
		if(ret != null){
			if(DEBUGMODE)
				Log.i(DEBUGTAG, "MapTileCache succeded for: " + aTileURLString);
		}else{
			if(DEBUGMODE)
				Log.i(DEBUGTAG, "Cache failed, trying from FS.");
			try {
				this.mFSTileCache.loadMapTileToMemCacheAsync(aTileURLString, this.mLoadCallbackHandler);
				ret = this.mLoadingMapTile;
			} catch (Exception e) {
				if(DEBUGMODE)
					Log.d(DEBUGTAG, "Error(" + e.getClass().getSimpleName() + ") loading MapTile from Filesystem: " + OSMMapTileNameFormatter.format(aTileURLString));
			}
			if(ret == null){ /* FS did not contain the MapTile, we need to download it asynchronous. */
				if(DEBUGMODE)
					Log.i(DEBUGTAG, "Requesting Maptile for download.");
				ret = this.mLoadingMapTile;
							
				this.mTileProvider.requestMapTileAsync(coords, zoomLevel, this.mLoadCallbackHandler);
			}
		}
		return ret;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class LoadCallbackHandler extends Handler{
		@Override
		public void handleMessage(final Message msg) {
			final int what = msg.what;
			switch(what){
				case OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID:
					if(OSMMapTileManager.this.mDownloadFinishedListenerHander != null)
						OSMMapTileManager.this.mDownloadFinishedListenerHander.sendEmptyMessage(OSMAbstractMapTileProvider.MAPTILEPROVIDER_SUCCESS_ID);
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "MapTile download success.");
					break;
				case OSMAbstractMapTileProvider.MAPTILEPROVIDER_FAIL_ID:
					if(DEBUGMODE)
						Log.e(DEBUGTAG, "MapTile download error.");
					break;
					
				case OSMMapTileFilesystemCache.MAPTILEFSCACHE_SUCCESS_ID:
					if(OSMMapTileManager.this.mDownloadFinishedListenerHander != null)
						OSMMapTileManager.this.mDownloadFinishedListenerHander.sendEmptyMessage(OSMMapTileFilesystemCache.MAPTILEFSCACHE_SUCCESS_ID);
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "MapTile fs->cache success.");
					break;
				case OSMMapTileFilesystemCache.MAPTILEFSCACHE_FAIL_ID:
					if(DEBUGMODE)
						Log.e(DEBUGTAG, "MapTile download error.");
					break;
			}
		}
	}
}
