// Created by plusminus on 21:46:41 - 25.09.2008
package org.andnav.osm.tileprovider;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.andnav.osm.views.util.IMapTileFilenameProvider;
import org.andnav.osm.views.util.OpenStreetMapTileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;

/**
 * 
 * @author Nicolas Gramlich
 * 
 */
public class OpenStreetMapTileFilesystemProvider extends
		OpenStreetMapAsyncTileProvider {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileFilesystemProvider.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<ZipFile> mZipFiles = new ArrayList<ZipFile>();

	/** whether we have a data connection */
	private boolean mConnected = true;

	/** whether the sdcard is mounted read/write */
	private boolean mSdCardAvailable = true;

	/** keep around to unregister when we're done */
	private final IRegisterReceiver aRegisterReceiver;
	private final MyBroadcastReceiver mBroadcastReceiver;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * The tiles may be found on several media. This one works with tiles stored
	 * on the file system. It and its friends are typically created and
	 * controlled by {@link OpenStreetMapTileProvider}.
	 * 
	 * @param aCallback
	 * @param aRegisterReceiver
	 */
	public OpenStreetMapTileFilesystemProvider(
			final IRegisterReceiver aRegisterReceiver,
			IMapTileFilenameProvider pMapTileFilenameProvider) {
		super(NUMBER_OF_TILE_FILESYSTEM_THREADS,
				TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE, pMapTileFilenameProvider);

		this.aRegisterReceiver = aRegisterReceiver;
		mBroadcastReceiver = new MyBroadcastReceiver();

		checkSdCard();
		findZipFiles();

		final IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		aRegisterReceiver.registerReceiver(mBroadcastReceiver, networkFilter);

		final IntentFilter mediaFilter = new IntentFilter();
		mediaFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		mediaFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mediaFilter.addDataScheme("file");
		aRegisterReceiver.registerReceiver(mBroadcastReceiver, mediaFilter);
	}

	public void detach() {
		aRegisterReceiver.unregisterReceiver(mBroadcastReceiver);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected boolean getShouldTilesBeSavedInCache() {
		return false;
	}

	@Override
	protected boolean getUsesDataConnection() {
		return false;
	}

	@Override
	protected String threadGroupName() {
		return "filesystem";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	};

	@Override
	public void stopWorkers() {
		super.stopWorkers();
	}

	private void findZipFiles() {

		mZipFiles.clear();

		final File[] z = OSMDROID_PATH.listFiles(new FileFilter() {
			@Override
			public boolean accept(final File aFile) {
				return aFile.isFile() && aFile.getName().endsWith(".zip");
			}
		});

		if (z != null) {
			for (final File file : z) {
				try {
					mZipFiles.add(new ZipFile(file));
				} catch (final Throwable e) {
					logger.warn("Error opening zip file: " + file, e);
				}
			}
		}
	}

	private synchronized InputStream fileFromZip(final OpenStreetMapTile aTile) {
		final String path = getMapTileFilenameProvider().getOutputFile(aTile)
				.getPath();
		for (final ZipFile zipFile : mZipFiles) {
			try {
				final ZipEntry entry = zipFile.getEntry(path);
				if (entry != null) {
					final InputStream in = zipFile.getInputStream(entry);
					return in;
				}
			} catch (final Throwable e) {
				logger.warn("Error getting zip stream: " + aTile, e);
			}
		}

		return null;
	}

	private void checkSdCard() {
		final String state = Environment.getExternalStorageState();
		logger.info("sdcard state: " + state);
		mSdCardAvailable = Environment.MEDIA_MOUNTED.equals(state);
		if (DEBUGMODE)
			logger.debug("mSdcardAvailable=" + mSdCardAvailable);
		if (!mSdCardAvailable) {
			mZipFiles.clear();
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends OpenStreetMapAsyncTileProvider.TileLoader {

		/**
		 * The tile loading policy for deciding which file to use... The order
		 * of preferences is... prefer actual tiles over dummy tiles prefer
		 * newest tile over older prefer local tiles over zip prefer zip files
		 * in lexicographic order
		 * 
		 * When a dummy tile is generated it may be constructed from coarser
		 * tiles from a lower resolution level.
		 * 
		 * aTile a tile to be constructed by the method.
		 */
		@Override
		public void loadTile(final OpenStreetMapTileRequestState aState,
				TileLoadResult aResult) {

			OpenStreetMapTile aTile = aState.getMapTile();

			// if there's no sdcard then don't do anything
			if (!mSdCardAvailable) {
				if (DEBUGMODE)
					logger.debug("No sdcard - do nothing for tile: " + aTile);
				aResult.setFailureResult();
				return;
			}

			final File tileFile = getMapTileFilenameProvider().getOutputFile(
					aTile);

			try {
				if (tileFile.exists()) {
					if (DEBUGMODE)
						logger.debug("Loaded tile: " + aTile);
					tileLoaded(aState, tileFile.getPath());
					aResult.setSuccessResult();

					// check for old tile
					final long now = System.currentTimeMillis();
					final long lastModified = tileFile.lastModified();
					if (now - lastModified > TILE_EXPIRY_TIME_MILLISECONDS) {
						aResult.setFailureResult();

						// XXX perhaps we should distinguish between phone and
						// wifi data connection
						// TODO: re-enable this?
						// if (mConnected && mCallback.useDataConnection()) {
						// if (DEBUGMODE)
						// logger
						// .debug("Tile has expired, requesting new download: "
						// + aTile);
						// // mTileDownloader.loadMapTileAsync(aTile);
						// aResult.setFailureResult();
						// } else {
						// if (DEBUGMODE)
						// logger
						// .debug("Tile has expired - not connected - not downloading: "
						// + aTile);
						// }
					}

				} else {

					if (DEBUGMODE)
						logger.debug("Tile doesn't exist: " + aTile);

					final InputStream fileFromZip = fileFromZip(aTile);
					if (fileFromZip == null) {
						aResult.setFailureResult();

						// XXX perhaps we should distinguish between phone and
						// wifi data connection
						// if (mConnected && mCallback.useDataConnection()) {
						// if (DEBUGMODE)
						// logger.debug("Request for download: " + aTile);
						// // mTileDownloader.loadMapTileAsync(aTile);
						// } else {
						// if (DEBUGMODE)
						// logger
						// .debug("Not connected - not downloading: "
						// + aTile);
						// }

						// don't refresh the screen because there's nothing new
						// tileLoaded(aTile, false);
						aResult.setFailureResult();
					} else {
						if (DEBUGMODE)
							logger.debug("Use tile from zip: " + aTile);
						// tileLoaded(aTile, fileFromZip);
						tileLoaded(aState, fileFromZip);
						aResult.setSuccessResult();
					}
				}
			} catch (final Throwable e) {
				logger.error("Error loading tile", e);
				// tileLoaded(aTile, false);
				aResult.setFailureResult();
			}
		}
	}

	/**
	 * This broadcast receiver is responsible for determining the best channel
	 * over which tiles may be acquired. In other words it sets network status
	 * flags.
	 * 
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(final Context aContext, final Intent aIntent) {

			final String action = aIntent.getAction();
			logger.info("onReceive: " + action);

			final WifiManager wm = (WifiManager) aContext
					.getSystemService(Context.WIFI_SERVICE);
			final int wifiState = wm.getWifiState(); // TODO check for
			// permission or catch
			// error
			if (DEBUGMODE)
				logger.debug("wifi state=" + wifiState);

			final TelephonyManager tm = (TelephonyManager) aContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			final int dataState = tm.getDataState(); // TODO check for
			// permission or catch
			// error
			if (DEBUGMODE)
				logger.debug("telephone data state=" + dataState);

			mConnected = wifiState == WifiManager.WIFI_STATE_ENABLED
					|| dataState == TelephonyManager.DATA_CONNECTED;

			if (DEBUGMODE)
				logger.debug("mConnected=" + mConnected);

			checkSdCard();

			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				findZipFiles();
			}
		}
	}
}
