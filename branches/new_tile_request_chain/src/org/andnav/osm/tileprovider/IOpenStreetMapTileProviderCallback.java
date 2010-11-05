package org.andnav.osm.tileprovider;

import java.io.InputStream;

public interface IOpenStreetMapTileProviderCallback {

	/**
	 * The map tile request has completed.
	 * 
	 * @param aTile
	 *            the tile request that has completed
	 * @param aTilePath
	 *            the path of the requested tile, or null if request has
	 *            completed without returning a tile path
	 */
	void mapTileRequestCompleted(OpenStreetMapTileRequestState aState,
			String aTilePath);

	/**
	 * The map tile request has completed.
	 * 
	 * @param aTile
	 *            the tile request that has completed
	 * @param aTileInputStream
	 *            the input stream of the requested tile, or null if request has
	 *            completed without returning a tile
	 */
	void mapTileRequestCompleted(OpenStreetMapTileRequestState aState,
			final InputStream aTileInputStream);

	/**
	 * The map tile request has completed but no tile has loaded.
	 * 
	 * @param aTile
	 *            the tile request that has completed
	 */
	void mapTileRequestCompleted(OpenStreetMapTileRequestState aState);

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
