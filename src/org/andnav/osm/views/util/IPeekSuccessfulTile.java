package org.andnav.osm.views.util;

import java.io.InputStream;

import org.andnav.osm.tileprovider.OpenStreetMapTileRequestState;

public interface IPeekSuccessfulTile {

	void peekAtSuccessfulTile(OpenStreetMapTileRequestState pState,
			InputStream pStream);

	void peekAtSuccessfulTile(OpenStreetMapTileRequestState pState,
			String pFilename);
}
