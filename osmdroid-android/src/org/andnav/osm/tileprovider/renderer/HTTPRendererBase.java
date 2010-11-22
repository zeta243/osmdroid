package org.andnav.osm.tileprovider.renderer;

import org.andnav.osm.tileprovider.CloudmadeException;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCloudmadeTokenCallback;
import org.andnav.osm.tileprovider.OpenStreetMapTile;

public abstract class HTTPRendererBase extends OpenStreetMapRendererBase {
	private final String mBaseUrls[];
	protected int cloudmadeStyle = 1;
	private int mMinimumZoomLevel;
	private int mMaximumZoomLevel;

	public HTTPRendererBase(String aName, int aZoomMinLevel, int aZoomMaxLevel,
			int aMaptileZoom, String aImageFilenameEnding,
			final String... aBaseUrl) {
		super(aName, aMaptileZoom, aImageFilenameEnding);
		mMinimumZoomLevel = aZoomMinLevel;
		mMaximumZoomLevel = aZoomMaxLevel;
		mBaseUrls = aBaseUrl;
	}

	public abstract String getTileURLString(
			OpenStreetMapTile aTile,
			IOpenStreetMapTileProviderCloudmadeTokenCallback aCloudmadeTokenCallback)
			throws CloudmadeException;

	public void setCloudmadeStyle(int aStyleId) {
		cloudmadeStyle = aStyleId;
	}

	public int getMinimumZoomLevel() {
		return mMinimumZoomLevel;
	}

	public int getMaximumZoomLevel() {
		return mMaximumZoomLevel;
	}

	/**
	 * Get the base url, which will be a random one if there are more than one.
	 */
	protected String getBaseUrl() {
		return mBaseUrls[random.nextInt(mBaseUrls.length)];
	}
}
