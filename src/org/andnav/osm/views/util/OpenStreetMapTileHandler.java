package org.andnav.osm.views.util;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;

public abstract class OpenStreetMapTileHandler {

	public static final int MAPTILEDOWNLOADER_SUCCESS_ID = 0;
	public static final int MAPTILEDOWNLOADER_FAIL_ID = MAPTILEDOWNLOADER_SUCCESS_ID + 1;
	protected HashSet<String> mPending = new HashSet<String>();
	protected Context mCtx;
	protected OpenStreetMapTileFilesystemProvider mMapTileFSProvider;
	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(5);
	protected OpenStreetMapRendererInfo mRendererInfo;

	public OpenStreetMapTileHandler(final Context ctx, OpenStreetMapRendererInfo aRendererInfo, final OpenStreetMapTileFilesystemProvider aMapTileFSProvider){
		this.mCtx = ctx;
		this.mMapTileFSProvider = aMapTileFSProvider;
		this.mRendererInfo = aRendererInfo;
	}
	
	public abstract void requestMapTileAsync(int[] coords,int zoomLevel, final Handler callback);

}