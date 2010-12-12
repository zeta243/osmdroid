package org.andnav.osm.tileprovider.renderer;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.ResourceProxy.string;
import org.andnav.osm.tileprovider.CloudmadeException;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCloudmadeTokenCallback;
import org.andnav.osm.tileprovider.OpenStreetMapTile;

class XYRenderer extends HTTPRendererBase {

	private final ResourceProxy.string mResourceId;

	XYRenderer(String aName, string aResourceId, int aZoomMinLevel,
			int aZoomMaxLevel, String aImageFilenameEnding, String... aBaseUrl) {
		super(aName, aZoomMinLevel, aZoomMaxLevel, aImageFilenameEnding,
				aBaseUrl);
		mResourceId = aResourceId;
	}

	@Override
	public String localizedName(ResourceProxy proxy) {
		return proxy.getString(mResourceId);
	}

	@Override
	public String getTileURLString(
			OpenStreetMapTile aTile,
			IOpenStreetMapTileProviderCloudmadeTokenCallback aCloudmadeTokenCallback)
			throws CloudmadeException {
		return getBaseUrl() + aTile.getZoomLevel() + "/" + aTile.getX() + "/"
				+ aTile.getY() + mImageFilenameEnding;
	}
}
