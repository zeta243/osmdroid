package org.andnav.osm.tileprovider.renderer;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

import org.andnav.osm.tileprovider.OpenStreetMapTile;
import org.andnav.osm.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public abstract class OpenStreetMapRendererBase implements
		IOpenStreetMapRendererInfo, OpenStreetMapTileProviderConstants {

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapRendererBase.class);

	private static int globalOrdinal = 0;

	private final String mBaseUrls[];
	private int mMinimumZoomLevel;
	private int mMaximumZoomLevel;

	private final int mOrdinal;
	protected final String mName;
	protected final String mImageFilenameEnding;
	protected final Random random = new Random();

	private final int mTileSizePixels;

	public OpenStreetMapRendererBase(String aName, int aZoomMinLevel,
			int aZoomMaxLevel, int aTileSizePixels,
			String aImageFilenameEnding, final String... aBaseUrl) {
		mOrdinal = globalOrdinal++;
		mName = aName;
		mMinimumZoomLevel = aZoomMinLevel;
		mMaximumZoomLevel = aZoomMaxLevel;
		mTileSizePixels = aTileSizePixels;
		mImageFilenameEnding = aImageFilenameEnding;
		mBaseUrls = aBaseUrl;
	}

	@Override
	public int ordinal() {
		return mOrdinal;
	}

	@Override
	public String name() {
		return mName;
	}

	public String pathBase() {
		return mName;
	}

	public String imageFilenameEnding() {
		return mImageFilenameEnding;
	}

	public abstract String getTileURLString(OpenStreetMapTile aTile);

	public int getMinimumZoomLevel() {
		return mMinimumZoomLevel;
	}

	public int getMaximumZoomLevel() {
		return mMaximumZoomLevel;
	}

	public int getTileSizePixels() {
		return mTileSizePixels;
	}

	/**
	 * Get the base url, which will be a random one if there are more than one.
	 */
	protected String getBaseUrl() {
		return mBaseUrls[random.nextInt(mBaseUrls.length)];
	}

	@Override
	public Drawable getDrawable(final String aFilePath) {
		try {
			// default implementation will load the file as a bitmap and create
			// a BitmapDrawable from it
			final Bitmap bitmap = BitmapFactory.decodeFile(aFilePath);
			if (bitmap != null) {
				return new BitmapDrawable(bitmap);
			} else {
				// if we couldn't load it then it's invalid - delete it
				try {
					new File(aFilePath).delete();
				} catch (final Throwable e) {
					logger.error("Error deleting invalid file: " + aFilePath, e);
				}
			}
		} catch (final OutOfMemoryError e) {
			logger.error("OutOfMemoryError loading bitmap: " + aFilePath);
			System.gc();
		}
		return null;
	}

	@Override
	public String getTileRelativeFilenameString(OpenStreetMapTile tile) {
		StringBuilder sb = new StringBuilder();
		sb.append(pathBase());
		sb.append('/');
		sb.append(tile.getZoomLevel());
		sb.append('/');
		sb.append(tile.getX());
		sb.append('/');
		sb.append(tile.getY());
		sb.append(imageFilenameEnding());
		sb.append(TILE_PATH_EXTENSION);
		return sb.toString();
	}

	@Override
	public Drawable getDrawable(final InputStream aFileInputStream) {
		try {
			// default implementation will load the file as a bitmap and create
			// a BitmapDrawable from it
			final Bitmap bitmap = BitmapFactory.decodeStream(aFileInputStream);
			if (bitmap != null) {
				return new BitmapDrawable(bitmap);
			}
		} catch (final OutOfMemoryError e) {
			logger.error("OutOfMemoryError loading bitmap");
			System.gc();
		}
		return null;
	}
}
