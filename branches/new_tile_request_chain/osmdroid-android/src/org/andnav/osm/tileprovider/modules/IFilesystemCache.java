package org.andnav.osm.tileprovider.modules;

import java.io.InputStream;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;

public interface IFilesystemCache {
	boolean saveFile(final IOpenStreetMapRendererInfo pRenderInfo,
			OpenStreetMapTile pTile, final InputStream pStream);
}
