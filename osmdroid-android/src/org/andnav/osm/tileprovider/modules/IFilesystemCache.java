package org.andnav.osm.tileprovider.modules;

import java.io.InputStream;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;

/**
 * Represents a write-only interface into a file system cache.
 * 
 * @author Marc
 * 
 */
public interface IFilesystemCache {
	/**
	 * Save an InputStream as the specified tile in the file system cache for
	 * the specified renderer.
	 * 
	 * @param pRenderInfo
	 *            a renderer
	 * @param pTile
	 *            a tile
	 * @param pStream
	 *            an InputStream
	 * @return
	 */
	boolean saveFile(final IOpenStreetMapRendererInfo pRenderInfo,
			OpenStreetMapTile pTile, final InputStream pStream);
}
