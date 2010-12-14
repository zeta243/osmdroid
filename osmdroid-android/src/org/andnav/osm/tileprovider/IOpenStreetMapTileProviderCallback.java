package org.andnav.osm.tileprovider;


import android.graphics.drawable.Drawable;

public interface IOpenStreetMapTileProviderCallback {

	/**
	 * The map tile request has completed.
	 * 
	 * @param aTile
	 *            the tile request that has completed, or null if tile was not
	 *            loaded (but request was successful?)
	 */
	void mapTileRequestCompleted(OpenStreetMapTileRequestState aState,
			final Drawable aDrawable);

	/**
	 * The map tile request has completed but no tile has loaded.
	 * 
	 * @param aTile
	 *            the tile request that has completed
	 */
	void mapTileRequestFailed(OpenStreetMapTileRequestState aState);

	/**
	 * Whether to use the network connection if it's available.
	 */
	public boolean useDataConnection();
}
