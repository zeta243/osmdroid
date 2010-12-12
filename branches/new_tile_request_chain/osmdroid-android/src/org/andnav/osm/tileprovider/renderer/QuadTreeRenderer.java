package org.andnav.osm.tileprovider.renderer;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.ResourceProxy.string;
import org.andnav.osm.tileprovider.CloudmadeException;
import org.andnav.osm.tileprovider.OpenStreetMapTile;

class QuadTreeRenderer extends OpenStreetMapRendererBase {

	private final ResourceProxy.string mResourceId;

	QuadTreeRenderer(String aName, string aResourceId, int aZoomMinLevel,
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
	public String getTileURLString(OpenStreetMapTile aTile)
			throws CloudmadeException {
		return getBaseUrl() + quadTree(aTile) + mImageFilenameEnding;
	}

	/**
	 * Converts TMS tile coordinates to QuadTree
	 * 
	 * @param aTile
	 *            The tile coordinates to convert
	 * @return The QuadTree as String.
	 */
	private String quadTree(final OpenStreetMapTile aTile) {
		final StringBuilder quadKey = new StringBuilder();
		for (int i = aTile.getZoomLevel(); i > 0; i--) {
			int digit = 0;
			int mask = 1 << (i - 1);
			if ((aTile.getX() & mask) != 0)
				digit += 1;
			if ((aTile.getY() & mask) != 0)
				digit += 2;
			quadKey.append("" + digit);
		}

		return quadKey.toString();
	}

}
