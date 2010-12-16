package org.andnav.osm.tileprovider.modules;

import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.TileProviderBase;

public class FileArchiveProvider extends TileProviderBase {

	public FileArchiveProvider(final IOpenStreetMapTileProviderCallback aCallback) {
		super(aCallback);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String threadGroupName() {
		return "filearchive";
	}

	@Override
	protected Runnable getTileLoader() {
		return null; // FIXME implementation
		// return new TileLoader();
	}

}
