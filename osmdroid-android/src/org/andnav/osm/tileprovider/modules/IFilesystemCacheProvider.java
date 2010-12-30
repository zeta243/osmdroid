package org.andnav.osm.tileprovider.modules;

import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;

/**
 * An interface that allows a class to register a renderer for file system cache
 * access. This allows that renderer to store data in the file system cache, and
 * have it rendered by the renderer later when it is needed.
 * 
 * @author Marc Kurtz
 * 
 */
@Deprecated
public interface IFilesystemCacheProvider {
	IFilesystemCache registerRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo, int minimumZoomLevel,
			int maximumZoomLevel);

	void unregisterRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo);
}
