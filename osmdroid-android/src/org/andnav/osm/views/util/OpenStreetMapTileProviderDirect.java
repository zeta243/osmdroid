package org.andnav.osm.views.util;

import org.andnav.osm.tileprovider.CloudmadeDefaultTokenProvider;
import org.andnav.osm.tileprovider.IOpenStreetMapTileProviderCallback;
import org.andnav.osm.tileprovider.IRegisterReceiver;
import org.andnav.osm.tileprovider.OpenStreetMapTileDownloader;
import org.andnav.osm.tileprovider.OpenStreetMapTileFilesystemProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Handler;

/**
 * Objects of this class provide access to tiles which are amenable to
 * synchronous access. They are expected to return quickly enough so that a
 * person will perceive it as instantaneous.
 * 
 * At present the only source which meets this criteria is the file system.
 */
public class OpenStreetMapTileProviderDirect extends
		OpenStreetMapTileProviderArray implements
		IOpenStreetMapTileProviderCallback {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileProviderDirect.class);

	// private final String mCloudmadeKey;
	private final OpenStreetMapTileFilesystemProvider mFileSystemProvider;
	private OpenStreetMapTileDownloader mTileDownloaderProvider;

	public OpenStreetMapTileProviderDirect(
			final Handler pDownloadFinishedListener,
			final String aCloudmadeKey,
			final IRegisterReceiver aRegisterReceiver) {
		super(pDownloadFinishedListener, aRegisterReceiver);
		IMapTileFilenameProvider mapTileFilenameProvider = new DefaultMapTileFilenameProvider();
		mFileSystemProvider = new OpenStreetMapTileFilesystemProvider(
				aRegisterReceiver, mapTileFilenameProvider);
		mTileDownloaderProvider = new OpenStreetMapTileDownloader(
				new CloudmadeDefaultTokenProvider(aCloudmadeKey),
				mapTileFilenameProvider);
		super.mTileProviderList.add(mFileSystemProvider);
		super.mTileProviderList.add(mTileDownloaderProvider);
	}
	// @Override
	// public void detach() {
	// mFileSystemProvider.detach();
	// }

	// @Override
	// public Drawable getMapTile(final OpenStreetMapTile pTile) {
	// if (mTileCache.containsTile(pTile)) {
	// if (DEBUGMODE)
	// logger.debug("MapTileCache succeeded for: " + pTile);
	// return mTileCache.getMapTile(pTile);
	// } else {
	// if (DEBUGMODE)
	// logger.debug("Cache failed, trying from FS: " + pTile);
	// mFileSystemProvider.loadMapTileAsync(/* pTile */null);
	// return null;
	// }
	// }

	// @Override
	// public String getCloudmadeKey() throws CloudmadeException {
	// if (mCloudmadeKey == null || mCloudmadeKey.length() == 0) {
	// throw new CloudmadeException("Error getting Cloudmade key");
	// }
	// return mCloudmadeKey;
	// }
}
