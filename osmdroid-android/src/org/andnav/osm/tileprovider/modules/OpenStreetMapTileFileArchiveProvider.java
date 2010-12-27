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

import android.graphics.drawable.Drawable;

/**
 * A tile provider that can serve tiles from a Zip archive. The provider accepts
 * one or more renderers to use to find tiles in the archives. The tile provider
 * will automatically find existing archives and use each one that it finds.
 *
 * @author Marc Kurtz
 * @author Nicolas Gramlich
 *
 */
public class OpenStreetMapTileFileArchiveProvider extends
		OpenStreetMapTileFileStorageProviderBase {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileFileArchiveProvider.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<ZipFile> mZipFiles = new ArrayList<ZipFile>();

	private final IOpenStreetMapRendererInfo[] mRenderInfoArray;
	private int mMinimumZoomLevel = Integer.MAX_VALUE;
	private int mMaximumZoomLevel = Integer.MIN_VALUE;


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
			final IOpenStreetMapRendererInfo[] pRendererInfoArray,
			final IRegisterReceiver pRegisterReceiver) {
		super(NUMBER_OF_TILE_FILESYSTEM_THREADS,
				TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE, pRegisterReceiver);

		mRenderInfoArray = pRendererInfoArray;

		for(final IOpenStreetMapRendererInfo renderer : pRendererInfoArray) {
			if (mMinimumZoomLevel > renderer.getMinimumZoomLevel()) {
				mMinimumZoomLevel = renderer.getMinimumZoomLevel();
			}
			if (mMaximumZoomLevel < renderer.getMaximumZoomLevel()) {
				mMaximumZoomLevel = renderer.getMaximumZoomLevel();
			}
		}

		findZipFiles();
	}

	public OpenStreetMapTileFileArchiveProvider(
			final IOpenStreetMapRendererInfo pRendererInfo,
			final IRegisterReceiver pRegisterReceiver) {
		this(new IOpenStreetMapRendererInfo[] { pRendererInfo },
				pRegisterReceiver);
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
	protected String getName() {
		return "File Archive Provider";
	}

	@Override
	protected String getThreadGroupName() {
		return "filearchive";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	}

	@Override
	public int getMinimumZoomLevel() {
		return mMinimumZoomLevel;
	}

	@Override
	public int getMaximumZoomLevel() {
		return mMaximumZoomLevel;
	}

	@Override
	protected void onMediaMounted() {
		findZipFiles();
	}

	@Override
	protected void onMediaUnmounted() {
		findZipFiles();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// TODO: Make all this thread-safe and synchronized
	private void findZipFiles() {

		mZipFiles.clear();

		if (!getSdCardAvailable())
			return;

		// path should be optionally configurable
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

	/**
	 * Checks each renderer to see if its tile exists in an archive
	 *
	 * @param aTile
	 *            the tile to obtain
	 * @return a Drawable
	 */
	private synchronized Drawable drawableFromZip(final OpenStreetMapTile aTile) {
		for (final IOpenStreetMapRendererInfo renderer : mRenderInfoArray) {
			final String path = renderer.getTileRelativeFilenameString(aTile);
			for (final ZipFile zipFile : mZipFiles) {
				InputStream inputStream = null;
				try {
					final ZipEntry entry = zipFile.getEntry(path);
					if (entry != null) {
						inputStream = zipFile.getInputStream(entry);
						final Drawable drawable = renderer
								.getDrawable(inputStream);
						return drawable;
					}
				} catch (final Throwable e) {
					logger.warn("Error getting zip stream: " + aTile, e);
				} finally {
					if (inputStream != null)
						StreamUtils.closeStream(inputStream);
				}
			}
		}
		return null;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends
			OpenStreetMapTileModuleProviderBase.TileLoader {

		@Override
		public Drawable loadTile(final OpenStreetMapTileRequestState aState) {

			final OpenStreetMapTile aTile = aState.getMapTile();

			// if there's no sdcard then don't do anything
			if (!getSdCardAvailable()) {
				if (DEBUGMODE)
					logger.debug("No sdcard - do nothing for tile: " + aTile);
				return null;
			}

			try {
				final Drawable drawable = drawableFromZip(aTile);
				if (drawable != null) {
					if (DEBUGMODE)
						logger.debug("Use tile from zip: " + aTile);
					return drawable;
				}
			} catch (final Throwable e) {
				logger.error("Error loading tile", e);
			}
			return null;
		}
	}
}
