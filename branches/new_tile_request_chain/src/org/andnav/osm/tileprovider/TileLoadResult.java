package org.andnav.osm.tileprovider;


public class TileLoadResult {
	boolean mSuccess;
	OpenStreetMapAsyncTileProvider mProvider;

	public boolean isSuccess() {
		return mSuccess;
	}

	public OpenStreetMapAsyncTileProvider getProvider() {
		return mProvider;
	}

	protected TileLoadResult(OpenStreetMapAsyncTileProvider provider) {
		mProvider = provider;
		setFailureResult();
	}

	public void setFailureResult() {
		mSuccess = false;
	}

	public void setSuccessResult() {
		mSuccess = true;
	}
}
