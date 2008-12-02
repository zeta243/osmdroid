package org.andnav.osm.views.tiles;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;

public abstract class OSMAbstractMapTileProvider {

	// ===========================================================
	// Constants
	// ===========================================================
	
	public static final int MAPTILEPROVIDER_SUCCESS_ID = 0;
	public static final int MAPTILEPROVIDER_FAIL_ID = MAPTILEPROVIDER_SUCCESS_ID + 1;

	// ===========================================================
	// Fields
	// ===========================================================
	
	protected HashSet<String> mPending = new HashSet<String>();
	protected Context mCtx;
	protected OSMMapTileFilesystemCache mMapTileFSCache;
	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(5);
	protected OSMMapTileProviderInfo mProviderInfo;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OSMAbstractMapTileProvider(final Context ctx, final OSMMapTileProviderInfo aProviderInfo, final OSMMapTileFilesystemCache aMapTileFSProvider){
		this.mCtx = ctx;
		this.mMapTileFSCache = aMapTileFSProvider;
		this.mProviderInfo = aProviderInfo;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public void setProviderInfo(final OSMMapTileProviderInfo aProviderInfo){
		this.mProviderInfo = aProviderInfo;
	}

	// ===========================================================
	// Methods from/for SuperClass/Interfaces
	// ===========================================================
	
	public abstract boolean requestMapTileAsync(int[] coords,int zoomLevel, final Handler callback);

	// ===========================================================
	// Methods
	// ===========================================================
	
}