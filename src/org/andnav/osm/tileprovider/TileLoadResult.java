package org.andnav.osm.tileprovider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TileLoadResult {
	boolean mSuccess;
	InputStream mResult;

	public boolean isSuccess() {
		return mSuccess;
	}

	public InputStream getResult() {
		return mResult;
	}

	protected TileLoadResult() {
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
		FileInputStream stream;
		try {
			stream = new FileInputStream(fileName);
			setSuccessResult(stream);
		} catch (FileNotFoundException e) {
			mSuccess = false;
			mResult = null;
		}
	}
}
