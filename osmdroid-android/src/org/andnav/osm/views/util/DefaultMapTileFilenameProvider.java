package org.andnav.osm.views.util;

import java.io.File;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Environment;

public class DefaultMapTileFilenameProvider implements
		IMapTileFilenameProvider, OpenStreetMapTileProviderConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultMapTileFilenameProvider.class);

	/** whether the sdcard is mounted read/write */
	private boolean mSdCardAvailable = true;

	/**
	 * Get the file location for the tile.
	 * 
	 * @param tile
	 * @return
	 * @throws CantContinueException
	 *             if the directory containing the file doesn't exist and can't
	 *             be created
	 */
	public File getOutputFile(final OpenStreetMapTile tile)
	/* throws CantContinueException */{
		final File file = buildFullPath(tile);
		final File parent = file.getParentFile();
		if (!parent.exists() && !createFolderAndCheckIfExists(parent)) {
			checkSdCard();
			// throw new CantContinueException("Tile directory doesn't exist: "
			// + parent);
		}
		return file;
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

	private File buildFullPath(final OpenStreetMapTile tile) {
		return new File(TILE_PATH_BASE, buildPath(tile) + TILE_PATH_EXTENSION);
	}

	private String buildPath(final OpenStreetMapTile tile) {
		final IOpenStreetMapRendererInfo renderer = tile.getRenderer();
		StringBuilder sb = new StringBuilder();
		sb.append(renderer.pathBase());
		sb.append('/');
		sb.append(tile.getZoomLevel());
		sb.append('/');
		sb.append(tile.getX());
		sb.append('/');
		sb.append(tile.getY());
		sb.append(renderer.imageFilenameEnding());
		return sb.toString();
	}

	private void checkSdCard() {
		final String state = Environment.getExternalStorageState();
		logger.info("sdcard state: " + state);
		mSdCardAvailable = Environment.MEDIA_MOUNTED.equals(state);
		if (DEBUGMODE)
			logger.debug("mSdcardAvailable=" + mSdCardAvailable);
	}
}
