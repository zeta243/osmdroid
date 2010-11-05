package org.andnav.osm.tileprovider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TileLoadResult {
	boolean mSuccess;
	InputStream mResult;
	OpenStreetMapAsyncTileProvider mProvider;

	public boolean isSuccess() {
		return mSuccess;
	}

	public InputStream getResult() {
		return mResult;
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
		mResult = null;
	}

	public void setSuccessResult(InputStream stream) {
		mSuccess = true;
		mResult = stream;
	}

	public void setSuccessResult(String fileName) {
		InputStream stream;
		try {
			stream = new FileInputStream(fileName);
			setSuccessResult(stream);
		} catch (FileNotFoundException e) {
			mSuccess = false;
			mResult = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
