package org.andnav.osm.tileprovider;

import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.andnav.osm.tileprovider.util.CloudmadeUtil;

public class CloudmadeDefaultTokenProvider implements
		IOpenStreetMapTileProviderCloudmadeTokenCallback,
		OpenStreetMapTileProviderConstants {

	private final String mCloudmadeKey;

	public CloudmadeDefaultTokenProvider(String mCloudmadeKey) {
		// super();
		this.mCloudmadeKey = mCloudmadeKey;
	}

	@Override
	public String getCloudmadeKey() throws CloudmadeException {
		return mCloudmadeKey;
	}

	@Override
	public String getCloudmadeToken(String aKey) throws CloudmadeException {
		return CloudmadeUtil.getCloudmadeToken(aKey);
	}
}
