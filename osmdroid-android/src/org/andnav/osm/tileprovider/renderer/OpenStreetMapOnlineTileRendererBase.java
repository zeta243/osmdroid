package org.andnav.osm.tileprovider.renderer;

import org.andnav.osm.ResourceProxy.string;
import org.andnav.osm.tileprovider.OpenStreetMapTile;

public abstract class OpenStreetMapOnlineTileRendererBase extends
		OpenStreetMapBitmapRendererBase {

	private final String mBaseUrls[];

	public OpenStreetMapOnlineTileRendererBase(String aName,
			string aResourceId, int aZoomMinLevel, int aZoomMaxLevel,
			int aTileSizePixels, String aImageFilenameEnding,
			String... aBaseUrl) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel,
				aTileSizePixels, aImageFilenameEnding);
		mBaseUrls = aBaseUrl;
	}

	public abstract String getTileURLString(OpenStreetMapTile aTile);

	/**
	 * Get the base url, which will be a random one if there are more than one.
	 */
	protected String getBaseUrl() {
		return mBaseUrls[random.nextInt(mBaseUrls.length)];
	}
}
