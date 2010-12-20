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
import org.andnav.osm.tileprovider.renderer.OpenStreetMapOnlineTileRendererBase;
import org.andnav.osm.tileprovider.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;

/**
 *
 * @author Nicolas Gramlich
 *
 */
public class OpenStreetMapTileFileArchiveProvider extends
		OpenStreetMapTileFileStorageProviderBase implements
		IPreferredRenderChangedReceiver {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileFileArchiveProvider.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final ArrayList<ZipFile> mZipFiles = new ArrayList<ZipFile>();

	private IOpenStreetMapRendererInfo mRenderInfo;

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
			final OpenStreetMapOnlineTileRendererBase pRendererInfo,
			final IRegisterReceiver pRegisterReceiver) {
		super(NUMBER_OF_TILE_FILESYSTEM_THREADS,
				TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE, pRegisterReceiver);

		mRenderInfo = pRendererInfo;

		findZipFiles();
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
		return mRenderInfo.getMinimumZoomLevel();
	}

	@Override
	public int getMaximumZoomLevel() {
		return mRenderInfo.getMaximumZoomLevel();
	}

	@Override
	protected void onMediaMounted() {
		findZipFiles();
	}

	@Override
	protected void onMediaUnmounted() {
		findZipFiles();
	}

	@Override
	public void onPreferredRendererChanged(final IOpenStreetMapRendererInfo pRenderer) {
		mRenderInfo = pRenderer;
	}

	// ===========================================================
	// Methods
	// ===========================================================

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

			InputStream fileFromZip = null;
			try {
				if (DEBUGMODE)
					logger.debug("Tile doesn't exist: " + aTile);

				fileFromZip = fileFromZip(aTile);
				if (fileFromZip != null) {
					if (DEBUGMODE)
						logger.debug("Use tile from zip: " + aTile);
					final Drawable drawable = mRenderInfo.getDrawable(fileFromZip);
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
}
