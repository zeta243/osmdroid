package org.andnav.osm.tileprovider;

import java.io.InputStream;

import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;

public interface IFilesystemCache {
	boolean saveFile(final IOpenStreetMapRendererInfo pRenderInfo,
			OpenStreetMapTile pTile, final InputStream pStream);
}
