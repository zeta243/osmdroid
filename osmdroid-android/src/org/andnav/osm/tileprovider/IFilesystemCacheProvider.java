package org.andnav.osm.tileprovider;

import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;

public interface IFilesystemCacheProvider {
	IFilesystemCache registerRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo);

	void unregisterRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo);
}
