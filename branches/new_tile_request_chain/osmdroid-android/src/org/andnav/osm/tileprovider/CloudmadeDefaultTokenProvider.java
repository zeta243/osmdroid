package org.andnav.osm.tileprovider;

import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.andnav.osm.tileprovider.util.CloudmadeUtil;

public class CloudmadeDefaultTokenProvider implements ICloudmadeTokenCallback,
		OpenStreetMapTileProviderConstants {

	private String mCloudmadeKey;
	private int mCloudmadeStyle;

	public CloudmadeDefaultTokenProvider(String aCloudmadeKey,
			int aCloudmadeStyle) {
		// super();
		this.mCloudmadeKey = aCloudmadeKey;
		mCloudmadeStyle = aCloudmadeStyle;
	}

	@Override
	public String getCloudmadeKey() throws CloudmadeException {
		return mCloudmadeKey;
	}

	public void setCloudmadeKey(String pCloudmadeKey) {
		mCloudmadeKey = pCloudmadeKey;
	}

	@Override
	public int getCloudmadeStyle() {
		return mCloudmadeStyle;
	}

	public void setCloudmadeStyle(int pCloudmadeStyle) {
		mCloudmadeStyle = pCloudmadeStyle;
	}

	@Override
	public String getCloudmadeToken(String aKey) throws CloudmadeException {
		return CloudmadeUtil.getCloudmadeToken(aKey);
	}
}
