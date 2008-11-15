// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav.osm.views.tiles;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.andnav.osm.util.StreamUtils;
import org.andnav.osm.util.constants.OSMConstants;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Nicolas Gramlich
 *
 */
public class OSMMapTileDownloadProvider extends OSMAbstractMapTileProvider implements OSMConstants, OpenStreetMapViewConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructor
	// ===========================================================
	
	public OSMMapTileDownloadProvider(final Context ctx, final OSMMapTileProviderInfo rendererInfo, final OSMMapTileFilesystemCache mapTileFSProvider) {
		super(ctx, rendererInfo, mapTileFSProvider);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	public void requestMapTileAsync(int[] coords, int zoomLevel, final Handler callback) {
		final String aURLString = mRendererInfo.getTileURLString(coords, zoomLevel);
		if(this.mPending.contains(aURLString))
			return;
	
		this.mPending.add(aURLString);
		getRemoteImageAsync(aURLString, callback);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	/** Sets the Child-ImageView of this to the URL passed. */
	public void getRemoteImageAsync(final String aURLString, final Handler callback) {
		this.mThreadPool.execute(new Runnable(){
			@Override
			public void run() {
				InputStream in = null;
				OutputStream out = null;

				try {
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "Downloading Maptile from url: " + aURLString);


					in = new BufferedInputStream(new URL(aURLString).openStream(), StreamUtils.IO_BUFFER_SIZE);

					final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
					out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
					StreamUtils.copy(in, out);
					out.flush();

					final byte[] data = dataStream.toByteArray();

					OSMMapTileDownloadProvider.this.mMapTileFSCache.saveFile(aURLString, data);
					if(DEBUGMODE)
						Log.i(DEBUGTAG, "Maptile saved to: " + aURLString);

					final Message successMessage = Message.obtain(callback, MAPTILEDOWNLOADER_SUCCESS_ID);
					successMessage.sendToTarget();
					OSMMapTileDownloadProvider.this.mPending.remove(aURLString);
				} catch (Exception e) {
					final Message failMessage = Message.obtain(callback, MAPTILEDOWNLOADER_FAIL_ID);
					failMessage.sendToTarget();
					if(DEBUGMODE)
						Log.e(DEBUGTAG, "Error Downloading MapTile. Exception: " + e.getClass().getSimpleName(), e);
					/* TODO What to do when downloading tile caused an error?
					 * Also remove it from the mPending?
					 * Doing not blocks it for the whole existence of this TileDownloder.
					 */
				} finally {
					StreamUtils.closeStream(in);
					StreamUtils.closeStream(out);
				}
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
