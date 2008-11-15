package org.andnav.osm.views.util;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;

public abstract class OSMMapTileAbstractProvider {

	// ===========================================================
	// Constants
	// ===========================================================
	
	public static final int MAPTILEDOWNLOADER_SUCCESS_ID = 0;
	public static final int MAPTILEDOWNLOADER_FAIL_ID = MAPTILEDOWNLOADER_SUCCESS_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected HashSet<String> mPending = new HashSet<String>();
	protected Context mCtx;
	protected OSMMapTileFilesystemCache mMapTileFSCache;
	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(5);
	protected OSMMapTileProviderInfo mRendererInfo;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OSMMapTileAbstractProvider(final Context ctx, OSMMapTileProviderInfo aRendererInfo, final OSMMapTileFilesystemCache aMapTileFSProvider){
		this.mCtx = ctx;
		this.mMapTileFSCache = aMapTileFSProvider;
		this.mRendererInfo = aRendererInfo;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from/for SuperClass/Interfaces
	// ===========================================================
	
	public abstract void requestMapTileAsync(int[] coords,int zoomLevel, final Handler callback);

	// ===========================================================
	// Methods
	// ===========================================================
	
}