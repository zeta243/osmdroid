// Created by plusminus on 21:46:41 - 25.09.2008
package org.andnav.osm.tileprovider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;
import org.andnav.osm.tileprovider.util.OpenStreetMapTileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * 
 * @author Nicolas Gramlich
 * 
 */
public class OpenStreetMapTileFilesystemProvider extends
		OpenStreetMapAsyncTileProvider implements IFilesystemCache,
		IFilesystemCacheProvider, OpenStreetMapTileProviderConstants {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileFilesystemProvider.class);

	// ===========================================================
	// Fields
	// ===========================================================

	/** whether the sdcard is mounted read/write */
	private boolean mSdCardAvailable = true;

	/** keep around to unregister when we're done */
	private final IRegisterReceiver aRegisterReceiver;
	private MyBroadcastReceiver mBroadcastReceiver;

	private int mMinimumZoomLevel = Integer.MAX_VALUE;
	private int mMaximumZoomLevel = Integer.MIN_VALUE;

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
			final IRegisterReceiver aRegisterReceiver) {
		super(NUMBER_OF_TILE_FILESYSTEM_THREADS,
				TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE, null);

		this.aRegisterReceiver = aRegisterReceiver;
		this.mBroadcastReceiver = new MyBroadcastReceiver();

		checkSdCard();

		final IntentFilter mediaFilter = new IntentFilter();
		mediaFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		mediaFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mediaFilter.addDataScheme("file");
		aRegisterReceiver.registerReceiver(mBroadcastReceiver, mediaFilter);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean getUsesDataConnection() {
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
	public void detach() {
		if (mBroadcastReceiver != null) {
			aRegisterReceiver.unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
		super.detach();
	}

	@Override
	public int getMinimumZoomLevel() {
		return mMinimumZoomLevel;
	}

	@Override
	public int getMaximumZoomLevel() {
		return mMaximumZoomLevel;
	}

	private void checkSdCard() {
		final String state = Environment.getExternalStorageState();
		logger.info("sdcard state: " + state);
		mSdCardAvailable = Environment.MEDIA_MOUNTED.equals(state);
		if (DEBUGMODE)
			logger.debug("mSdcardAvailable=" + mSdCardAvailable);
	}

	private void adjustMinimumMaximumZoomLevels(int pZoomLevel) {
		if (pZoomLevel < mMinimumZoomLevel)
			mMinimumZoomLevel = pZoomLevel;

		if (pZoomLevel > mMaximumZoomLevel)
			mMaximumZoomLevel = pZoomLevel;
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
		public Drawable loadTile(final OpenStreetMapTileRequestState aState) {

			OpenStreetMapTile aTile = aState.getMapTile();

			// if there's no sdcard then don't do anything
			if (!mSdCardAvailable) {
				if (DEBUGMODE)
					logger.debug("No sdcard - do nothing for tile: " + aTile);
				return null;
			}

			// Check each registered renderer to see if their file is available
			for (IOpenStreetMapRendererInfo renderInfo : mRenderInfoList) {
				File file = new File(TILE_PATH_BASE,
						renderInfo.getTileRelativeFilenameString(aTile));
				if (file.exists()) {
					Drawable drawable = renderInfo.getDrawable(file.getPath());
					return drawable;
				}
			}

			// TODO: Re-integrate the tile aging checks

			// try {
			// if (tileFile.exists()) {
			// if (DEBUGMODE)
			// logger.debug("Loaded tile: " + aTile);
			// tileLoaded(aState, tileFile.getPath());
			//
			// // check for old tile
			// final long now = System.currentTimeMillis();
			// final long lastModified = tileFile.lastModified();
			// if (now - lastModified > TILE_EXPIRY_TIME_MILLISECONDS) {
			// // This will trigger continuing with the tile provider
			// // change - this is currently safe to do after
			// // previously calling tileLoaded(), but maybe there is a
			// // better way to do this?
			// // tileLoadedFailed(aState);
			// return false;
			// } else
			// return true;
			//
			// } else {
			// if (DEBUGMODE)
			// logger.debug("Tile doesn't exist: " + aTile);
			// // tileLoadedFailed(aState);
			// }
			// } catch (final Throwable e) {
			// logger.error("Error loading tile", e);
			// // tileLoadedFailed(aState);
			// }

			return null;
		}
	}

	/**
	 * This broadcast receiver will recheck the sd card when the mount/unmount
	 * messages happen
	 * 
	 */
	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(final Context aContext, final Intent aIntent) {

			final String action = aIntent.getAction();
			logger.info("onReceive: " + action);

			checkSdCard();
		}
	}

	private boolean createFolderAndCheckIfExists(final File pFile) {
		if (pFile.mkdirs()) {
			return true;
		}
		if (DEBUGMODE)
			logger.debug("Failed to create " + pFile
					+ " - wait and check again");

		// if create failed, wait a bit in case another thread created it
		try {
			Thread.sleep(500);
		} catch (final InterruptedException ignore) {
		}
		// and then check again
		if (pFile.exists()) {
			if (DEBUGMODE)
				logger.debug("Seems like another thread created " + pFile);
			return true;
		} else {
			if (DEBUGMODE)
				logger.debug("File still doesn't exist: " + pFile);
			return false;
		}
	}

	@Override
	public boolean saveFile(IOpenStreetMapRendererInfo pRenderInfo,
			OpenStreetMapTile pTile, InputStream pStream) {

		adjustMinimumMaximumZoomLevels(pTile.getZoomLevel());

		File file = new File(TILE_PATH_BASE,
				pRenderInfo.getTileRelativeFilenameString(pTile));
		createFolderAndCheckIfExists(file.getParentFile());

		BufferedOutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(
					file.getPath()));
			StreamUtils.copy(pStream, outputStream);
		} catch (IOException e) {
			return false;
		} finally {
			if (outputStream != null)
				StreamUtils.closeStream(outputStream);
		}
		return true;
	}

	private final List<IOpenStreetMapRendererInfo> mRenderInfoList = new LinkedList<IOpenStreetMapRendererInfo>();

	@Override
	public IFilesystemCache registerRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo, int pMinimumZoomLevel,
			int pMaximumZoomLevel) {

		adjustMinimumMaximumZoomLevels(pMinimumZoomLevel);
		adjustMinimumMaximumZoomLevels(pMaximumZoomLevel);
		mRenderInfoList.add(pRendererInfo);
		return this;
	}

	@Override
	public void unregisterRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo) {
		mRenderInfoList.remove(pRendererInfo);
	}
}
