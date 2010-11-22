// Created by plusminus on 21:31:36 - 25.09.2008
package org.andnav.osm.tileprovider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.UnknownHostException;

import org.andnav.osm.tileprovider.renderer.HTTPRendererBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.Drawable;

/**
 * The OpenStreetMapTileDownloader loads tiles from a server and passes them to
 * a OpenStreetMapTileFilesystemProvider.
 * 
 * @author Nicolas Gramlich
 * @author Manuel Stahl
 * 
 */
public class OpenStreetMapTileDownloader extends OpenStreetMapAsyncTileProvider {

	// ===========================================================
	// Constants
	// ===========================================================

	private static final Logger logger = LoggerFactory
			.getLogger(OpenStreetMapTileDownloader.class);

	// ===========================================================
	// Fields
	// ===========================================================

	private IOpenStreetMapTileProviderCloudmadeTokenCallback mCallback;

	private final IFilesystemCacheProvider mFilesystemCacheProvider;

	private IFilesystemCache mFilesystemCache;

	private final HTTPRendererBase mRendererInfo;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OpenStreetMapTileDownloader(HTTPRendererBase pRendererInfo,
			final IOpenStreetMapTileProviderCloudmadeTokenCallback pCallback,
			IFilesystemCacheProvider pFilesystemCacheProvider) {
		super(NUMBER_OF_TILE_DOWNLOAD_THREADS,
				TILE_DOWNLOAD_MAXIMUM_QUEUE_SIZE, pFilesystemCacheProvider);
		mRendererInfo = pRendererInfo;
		mCallback = pCallback;
		mFilesystemCacheProvider = pFilesystemCacheProvider;
		if (mFilesystemCacheProvider != null)
			mFilesystemCache = mFilesystemCacheProvider
					.registerRendererForFilesystemAccess(mRendererInfo,
							mRendererInfo.getMinimumZoomLevel(), mRendererInfo
									.getMaximumZoomLevel());
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public boolean getUsesDataConnection() {
		return true;
	}

	@Override
	protected String threadGroupName() {
		return "downloader";
	}

	@Override
	protected Runnable getTileLoader() {
		return new TileLoader();
	};

	@Override
	public void detach() {
		if (mFilesystemCacheProvider != null)
			mFilesystemCacheProvider
					.unregisterRendererForFilesystemAccess(mRendererInfo);
		super.detach();
	}

	@Override
	public int getMinimumZoomLevel() {
		return mRendererInfo.getMinimumZoomLevel();
	}

	@Override
	public int getMaximumZoomLevel() {
		return mRendererInfo.getMaximumZoomLevel();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private String buildURL(final OpenStreetMapTile tile)
			throws CloudmadeException {
		return mRendererInfo.getTileURLString(tile, mCallback);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	private class TileLoader extends OpenStreetMapAsyncTileProvider.TileLoader {

		@Override
		public Drawable loadTile(final OpenStreetMapTileRequestState aState)
				throws CantContinueException {

			InputStream in = null;
			OutputStream out = null;
			OpenStreetMapTile tile = aState.getMapTile();

			try {
				final String tileURLString = buildURL(tile);

				if (DEBUGMODE)
					logger.debug("Downloading Maptile from url: "
							+ tileURLString);

				in = new BufferedInputStream(new URL(tileURLString)
						.openStream(), StreamUtils.IO_BUFFER_SIZE);

				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				out = new BufferedOutputStream(dataStream,
						StreamUtils.IO_BUFFER_SIZE);
				StreamUtils.copy(in, out);
				out.flush();
				final byte[] data = dataStream.toByteArray();
				ByteArrayInputStream byteStream = new ByteArrayInputStream(data);

				// Save the data to the filesystem cache
				if (mFilesystemCache != null) {
					mFilesystemCache.saveFile(mRendererInfo, tile, byteStream);
					byteStream.reset();
				}
				Drawable result = mRendererInfo.getDrawable(byteStream);

				return result;
			} catch (final UnknownHostException e) {
				// no network connection so empty the queue
				logger.warn("UnknownHostException downloading MapTile: " + tile
						+ " : " + e);
				throw new CantContinueException(e);
			} catch (final FileNotFoundException e) {
				logger.warn("Tile not found: " + tile + " : " + e);
			} catch (final IOException e) {
				logger.warn("IOException downloading MapTile: " + tile + " : "
						+ e);
			} catch (final CloudmadeException e) {
				logger.warn("CloudmadeException downloading MapTile: " + tile
						+ " : " + e);
			} catch (final Throwable e) {
				logger.error("Error downloading MapTile: " + tile, e);
			} finally {
				StreamUtils.closeStream(in);
				StreamUtils.closeStream(out);
			}

			return null;
		}
	}
}
