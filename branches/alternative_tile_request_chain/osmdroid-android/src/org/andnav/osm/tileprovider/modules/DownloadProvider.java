package org.andnav.osm.tileprovider.modules;

import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.TileProviderBase;

public class DownloadProvider extends TileProviderBase {

	public DownloadProvider(final IOpenStreetMapTileProviderCallback aCallback) {
		super(aCallback);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String threadGroupName() {
		return "downloader";
	}

	@Override
	protected Runnable getTileLoader() {
		return null; // FIXME implementation
		// return new TileLoader();
	}

}
