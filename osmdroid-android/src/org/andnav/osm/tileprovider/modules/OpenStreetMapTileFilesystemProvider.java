// Created by plusminus on 21:46:41 - 25.09.2008
package org.andnav.osm.tileprovider.modules;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.andnav.osm.tileprovider.IRegisterReceiver;
import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.OpenStreetMapTileRequestState;
import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.andnav.osm.tileprovider.renderer.IOpenStreetMapRendererInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;

/**
 * Implements a file system cache and provides cached tiles. This functions as a
 * tile provider by serving cached tiles. It also implements an
 * IFilesystemCacheProvider which can be used by other tile providers to
 * register for file system cache access so they can put their tiles in the file
 * system cache.
 *
 * @author Marc Kurtz
 * @author Nicolas Gramlich
 *
 */
public class OpenStreetMapTileFilesystemProvider extends
		OpenStreetMapTileFileStorageProviderBase implements
		IFilesystemCacheProvider, OpenStreetMapTileProviderConstants {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileFilesystemProvider.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private final long mMaximumCachedFileAge;

	private final TileWriter mTileWriter;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapTileFilesystemProvider(
			final IRegisterReceiver aRegisterReceiver) {
		this(aRegisterReceiver, DEFAULT_MAXIMUM_CACHED_FILE_AGE);
	}

	/**
	 * Provides a file system based cache tile provider. Other providers can
	 * register and store data in the cache.
	 *
	 * @param aRegisterReceiver
	 */
	public OpenStreetMapTileFilesystemProvider(
			final IRegisterReceiver aRegisterReceiver, final long maximumCachedFileAge) {
		super(NUMBER_OF_TILE_FILESYSTEM_THREADS,
				TILE_FILESYSTEM_MAXIMUM_QUEUE_SIZE, aRegisterReceiver);

		mMaximumCachedFileAge = maximumCachedFileAge;
		mTileWriter = new TileWriter();
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
		return "File System Cache Provider";
	}

	@Override
	protected String getThreadGroupName() {
		return "filesystem";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	};

	@Override
	public int getMinimumZoomLevel() {
		return mTileWriter.getMinimumZoomLevel();
	}

	@Override
	public int getMaximumZoomLevel() {
		return mTileWriter.getMaximumZoomLevel();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends
			OpenStreetMapTileModuleProviderBase.TileLoader {

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

			final OpenStreetMapTile aTile = aState.getMapTile();

			// if there's no sdcard then don't do anything
			if (!getSdCardAvailable()) {
				if (DEBUGMODE)
					logger.debug("No sdcard - do nothing for tile: " + aTile);
				return null;
			}

			// Check each registered renderer to see if their file is available
			// and if so, then render the drawable and return the tile
			for (final IOpenStreetMapRendererInfo renderInfo : mRenderInfoList) {
				final File file = new File(TILE_PATH_BASE, renderInfo.getTileRelativeFilenameString(aTile));
				if (file.exists()) {

					// Check to see if file has expired
					final long now = System.currentTimeMillis();
					final long lastModified = file.lastModified();
					final boolean fileExpired = lastModified < now - mMaximumCachedFileAge;

					if (!fileExpired) {
						// If the file has not expired, then render it and
						// return it!
						final Drawable drawable = renderInfo.getDrawable(file.getPath());
						return drawable;
					} else {
						// If the file has expired then we render it, but we
						// return it as a candidate and then fail on the
						// request. This allows the tile to be loaded, but also
						// allows other tile providers to do a better job.
						final Drawable drawable = renderInfo.getDrawable(file.getPath());
						tileCandidateLoaded(aState, drawable);
						return null;
					}
				}
			}

			// If we get here then there is no file in the file cache
			return null;
		}
	}

	private final List<IOpenStreetMapRendererInfo> mRenderInfoList = new LinkedList<IOpenStreetMapRendererInfo>();

	@Override
	public IFilesystemCache registerRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo, int pMinimumZoomLevel,
			int pMaximumZoomLevel) {

		mTileWriter.addZoomLevel(pMinimumZoomLevel);
		mTileWriter.addZoomLevel(pMaximumZoomLevel);
		mRenderInfoList.add(pRendererInfo);
		return mTileWriter;
	}

	@Override
	public void unregisterRendererForFilesystemAccess(
			IOpenStreetMapRendererInfo pRendererInfo) {
		mRenderInfoList.remove(pRendererInfo);
	}
}
