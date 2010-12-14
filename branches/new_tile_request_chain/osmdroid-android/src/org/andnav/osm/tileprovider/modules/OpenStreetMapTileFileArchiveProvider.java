// Created by plusminus on 21:46:41 - 25.09.2008
package org.andnav.osm.tileprovider.modules;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.andnav.osm.tileprovider.IRegisterReceiver;
import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.OpenStreetMapTileProviderBase;
import org.andnav.osm.tileprovider.OpenStreetMapTileRequestState;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;
import org.andnav.osm.tileprovider.util.StreamUtils;
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
public class OpenStreetMapTileFileArchiveProvider extends
		OpenStreetMapTileModuleProviderBase {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileFileArchiveProvider.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<ZipFile> mZipFiles = new ArrayList<ZipFile>();

	/** whether the sdcard is mounted read/write */
	private boolean mSdCardAvailable = true;

	/** keep around to unregister when we're done */
	private final IRegisterReceiver aRegisterReceiver;
	private MyBroadcastReceiver mBroadcastReceiver;

	private final IOpenStreetMapRendererInfo mRenderInfo;

	private final int mMinimumZoomLevel;

	private final int mMaximumZoomLevel;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * The tiles may be found on several media. This one works with tiles stored
	 * on the file system. It and its friends are typically created and
	 * controlled by {@link OpenStreetMapTileProviderBase}.
	 * 
	 * @param aCallback
	 * @param aRegisterReceiver
	 */
	public OpenStreetMapTileFileArchiveProvider(
			final IOpenStreetMapRendererInfo pRenderInfo,
			final IRegisterReceiver pRegisterReceiver,
			IFilesystemCacheProvider pFilesystemCacheProvider,
			int pMinimumZoomLevel, int pMaximumZoomLevel) {
		super(NUMBER_OF_TILE_FILESYSTEM_THREADS,
				TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE, pFilesystemCacheProvider);
		mRenderInfo = pRenderInfo;

		this.aRegisterReceiver = pRegisterReceiver;
		mMinimumZoomLevel = pMinimumZoomLevel;
		mMaximumZoomLevel = pMaximumZoomLevel;
		mBroadcastReceiver = new MyBroadcastReceiver();

		checkSdCard();
		findZipFiles();

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
		return "filearchive";
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
		final String path = mRenderInfo.getTileRelativeFilenameString(aTile);
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

	private class TileLoader extends OpenStreetMapTileModuleProviderBase.TileLoader {

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

			InputStream fileFromZip = null;
			try {
				if (DEBUGMODE)
					logger.debug("Tile doesn't exist: " + aTile);

				fileFromZip = fileFromZip(aTile);
				if (fileFromZip == null) {
					// tileLoadedFailed(aState);
				} else {
					if (DEBUGMODE)
						logger.debug("Use tile from zip: " + aTile);
					Drawable drawable = mRenderInfo.getDrawable(fileFromZip);
					return drawable;
				}
			} catch (final Throwable e) {
				logger.error("Error loading tile", e);
			} finally {
				if (fileFromZip != null)
					StreamUtils.closeStream(fileFromZip);
			}

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

			if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
				findZipFiles();
			}
		}
	}
}
