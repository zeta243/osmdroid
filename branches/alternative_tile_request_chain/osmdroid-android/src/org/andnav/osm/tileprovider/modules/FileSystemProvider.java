package org.andnav.osm.tileprovider.modules;

import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.TileProviderBase;

public class FileSystemProvider extends TileProviderBase {

	public FileSystemProvider(final IOpenStreetMapTileProviderCallback aCallback) {
		super(aCallback);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String threadGroupName() {
		return "filesystem";
	}

	@Override
	protected Runnable getTileLoader() {
		return null; // FIXME implementation
		// return new TileLoader();
	}

}
