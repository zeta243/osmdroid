// Created by plusminus on 21:46:22 - 25.09.2008
package org.andnav.osm.tileprovider.util;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.OpenStreetMapTileRequestState;
import org.andnav.osm.views.util.OpenStreetMapTileCache;
import org.andnav.osm.views.util.constants.OpenStreetMapViewConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;
import android.os.Handler;

/**
 * This is an abstract class. The tile provider is responsible for:
 * <ul>
 * <li>determining if a map tile is available,</li>
 * <li>notifying the client, via a callback handler</li>
 * </ul>
 * see {@link OpenStreetMapTile} for an overview of how tiles are served by this
 * provider.
 * 
 * @author Nicolas Gramlich
 * 
 */
public abstract class OpenStreetMapTileProvider implements
		OpenStreetMapViewConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileProvider.class);

	protected final OpenStreetMapTileCache mTileCache;
	protected final Handler mDownloadFinishedHandler;
	protected boolean mUseDataConnection = true;

	public abstract Drawable getMapTile(OpenStreetMapTile pTile);

	public abstract void detach();

	public OpenStreetMapTileProvider(final Handler pDownloadFinishedListener) {
		mTileCache = new OpenStreetMapTileCache();
		mDownloadFinishedHandler = pDownloadFinishedListener;
	}

	/**
	 * Called by implementation class methods indicating that they have
	 * completed the request as best it can. The tile is added to the cache. Let
	 * the renderer convert the file to a drawable.
	 * 
	 * @param pTile
	 *            the specification for the tile requested
	 * @param pTileInputStream
	 *            the open file stream to the tile image
	 */
	public void mapTileRequestCompleted(
			final OpenStreetMapTileRequestState pState, final Drawable pDrawable) {
		OpenStreetMapTile tile = pState.getMapTile();
		if (pDrawable != null) {
			mTileCache.putTile(tile, pDrawable);
		}

		// tell our caller we've finished and it should update its view
		mDownloadFinishedHandler
				.sendEmptyMessage(OpenStreetMapTile.MAPTILE_SUCCESS_ID);

		if (DEBUGMODE)
			logger.debug("MapTile request complete: " + tile);
	}

	/**
	 * Informs the caller that the image has been updated. This is typically
	 * called when the tile path or input stream have previously been posted.
	 * 
	 * @param pTile
	 *            the specification for the tile requested
	 */
	public void mapTileRequestFailed(final OpenStreetMapTileRequestState pState) {
		OpenStreetMapTile tile = pState.getMapTile();
		mDownloadFinishedHandler
				.sendEmptyMessage(OpenStreetMapTile.MAPTILE_FAIL_ID);

		if (DEBUGMODE)
			logger.debug("MapTile request failed: " + tile);
	}

	/**
	 * TODO:
	 * 
	 * @param aCapacity
	 */
	public void ensureCapacity(final int aCapacity) {
		mTileCache.ensureCapacity(aCapacity);
	}

	/**
	 * Whether to use the network connection if it's available.
	 */
	public boolean useDataConnection() {
		return mUseDataConnection;
	}

	/**
	 * Set whether to use the network connection if it's available.
	 * 
	 * @param aMode
	 *            if true use the network connection if it's available. if false
	 *            don't use the network connection even if it's available.
	 */
	public void setUseDataConnection(boolean aMode) {
		mUseDataConnection = aMode;
	}

}
