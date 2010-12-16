package org.andnav.osm.tileprovider.renderer;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.util.CloudmadeUtil;

class CloudmadeRenderer extends OpenStreetMapRendererBase {

	private final ResourceProxy.string mResourceId;
	private ICloudmadeTokenCallback mCloudmadeCallback;

	CloudmadeRenderer(ICloudmadeTokenCallback cloudmadeCallback, String aName,
			ResourceProxy.string aResourceId, int aZoomMinLevel,
			int aZoomMaxLevel, int aTileSizePixels,
			String aImageFilenameEnding, String... aBaseUrl) {
		super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
				aImageFilenameEnding, aBaseUrl);
		mCloudmadeCallback = cloudmadeCallback;
		mResourceId = aResourceId;
	}

	@Override
	public String pathBase() {
		if (mCloudmadeCallback.getCloudmadeStyle() <= 1) {
			return mName;
		} else {
			return mName + mCloudmadeCallback.getCloudmadeStyle();
		}
	}

	@Override
	public String localizedName(ResourceProxy proxy) {
		return proxy.getString(mResourceId);
	}

	@Override
	public String getTileURLString(OpenStreetMapTile aTile) {
		final String key;
		final String token;
		try {
			key = mCloudmadeCallback.getCloudmadeKey();
			token = getCloudmadeToken(key);
		} catch (CloudmadeException e) {
			// TODO Auto-generated catch block
			mCloudmadeCallback.onCloudmadeError(e);
			return null;
		}
		return String.format(getBaseUrl(), key,
				mCloudmadeCallback.getCloudmadeStyle(), getTileSizePixels(),
				aTile.getZoomLevel(), aTile.getX(), aTile.getY(),
				mImageFilenameEnding, token);
	}

	public String getCloudmadeToken(String aKey) throws CloudmadeException {
		return CloudmadeUtil.getCloudmadeToken(aKey);
	}
}
