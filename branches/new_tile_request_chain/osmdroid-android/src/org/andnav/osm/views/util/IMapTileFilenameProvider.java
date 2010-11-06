package org.andnav.osm.views.util;

import java.io.File;

import org.andnav.osm.tileprovider.OpenStreetMapTile;

public interface IMapTileFilenameProvider {
	File getOutputFile(final OpenStreetMapTile tile);
}
