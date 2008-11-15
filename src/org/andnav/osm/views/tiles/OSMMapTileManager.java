// Created by plusminus on 21:46:22 - 25.09.2008
package org.andnav.osm.views.tiles;

import org.andnav.osm.R;
import org.andnav.osm.util.constants.OSMConstants;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

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
public class OSMMapTileManager implements OSMConstants, OpenStreetMapViewConstants{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected final Bitmap mLoadingMapTile;
	protected Context mCtx;
	protected OSMMapTileMemoryCache mTileCache;
	protected OSMMapTileFilesystemCache mFSTileProvider;
	protected OSMAbstractMapTileProvider mTileMaker;
	private Handler mLoadCallbackHandler = new LoadCallbackHandler();
	private Handler mDownloadFinishedListenerHander;
	private OSMMapTileProviderInfo mRendererInfo;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OSMMapTileManager(final Context ctx, OSMMapTileProviderInfo renderInfo, final Handler aDownloadFinishedListener) {
		this.mCtx = ctx;
		this.mLoadingMapTile = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.maptile_loading);
		this.mTileCache = new OSMMapTileMemoryCache();
		this.mFSTileProvider = new OSMMapTileFilesystemCache(ctx, 4 * 1024 * 1024, this.mTileCache); // 4MB FSCache
		
		switch (renderInfo.PROVIDER_TYPE)
		{
		    case LOCAL_PROVIDER:
		    	this.mTileMaker = new OSMMapTileRenderProvider(ctx, renderInfo, this.mFSTileProvider);
		    	break;
		    case DOWNLOAD_PROVIDER:
		    default:
			    this.mTileMaker = new OSMMapTileDownloadProvider(ctx, renderInfo, this.mFSTileProvider);
			    break;
		}
		
		
		this.mDownloadFinishedListenerHander = aDownloadFinishedListener;
		this.mRendererInfo = renderInfo;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public Bitmap getMapTile(int[] coords, int zoomLevel)
	{
		final String aTileURLString = this.mRendererInfo.getTileURLString(coords, zoomLevel);
		
		Bitmap ret = this.mTileCache.getMapTile(aTileURLString);
		if(ret != null){
			if(DEBUGMODE)
				Log.i(DEBUGTAG, "MapTileCache succeded for: " + aTileURLString);
		}else{
			if(DEBUGMODE)
				Log.i(DEBUGTAG, "Cache failed, trying from FS.");
			try {
				this.mFSTileProvider.loadMapTileToMemCacheAsync(aTileURLString, this.mLoadCallbackHandler);
				ret = this.mLoadingMapTile;
			} catch (Exception e) {
				if(DEBUGMODE)
					Log.d(DEBUGTAG, "Error(" + e.getClass().getSimpleName() + ") loading MapTile from Filesystem: " + OSMMapTileNameFormatter.format(aTileURLString));
			}
			if(ret == null){ /* FS did not contain the MapTile, we need to download it asynchronous. */
				if(DEBUGMODE)
					Log.i(DEBUGTAG, "Requesting Maptile for download.");
				ret = this.mLoadingMapTile;
							
				this.mTileMaker.requestMapTileAsync(coords, zoomLevel, this.mLoadCallbackHandler);
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
				case OSMAbstractMapTileProvider.MAPTILEDOWNLOADER_SUCCESS_ID:
					OSMMapTileManager.this.mDownloadFinishedListenerHander.sendEmptyMessage(OSMAbstractMapTileProvider.MAPTILEDOWNLOADER_SUCCESS_ID);
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "MapTile download success.");
					break;
				case OSMAbstractMapTileProvider.MAPTILEDOWNLOADER_FAIL_ID:
					if(DEBUGMODE)
						Log.e(DEBUGTAG, "MapTile download error.");
					break;
					
				case OSMMapTileFilesystemCache.MAPTILEFSLOADER_SUCCESS_ID:
					OSMMapTileManager.this.mDownloadFinishedListenerHander.sendEmptyMessage(OSMMapTileFilesystemCache.MAPTILEFSLOADER_SUCCESS_ID);
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "MapTile fs->cache success.");
					break;
				case OSMMapTileFilesystemCache.MAPTILEFSLOADER_FAIL_ID:
					if(DEBUGMODE)
						Log.e(DEBUGTAG, "MapTile download error.");
					break;
			}
		}
	}

	public void preCacheTile(int[] coords, int zoomLevel) {
		getMapTile(coords, zoomLevel);
	}
	
	public void setRenderer(OSMMapTileProviderInfo aRendererInfo)
	{
		this.mRendererInfo = aRendererInfo;
	}
}
